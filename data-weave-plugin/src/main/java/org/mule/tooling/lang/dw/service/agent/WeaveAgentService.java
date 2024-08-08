package org.mule.tooling.lang.dw.service.agent;

import com.intellij.ProjectTopics;
import com.intellij.compiler.server.BuildManagerListener;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.compiler.DummyCompileContext;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Alarm;
import com.intellij.util.PathsList;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveRunnerHelper;
import org.mule.tooling.lang.dw.settings.DataWeaveSettingsState;
import org.mule.weave.v2.agent.api.event.*;
import org.mule.weave.v2.agent.client.*;
import org.mule.weave.v2.agent.client.tcp.TcpClientProtocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service(Service.Level.PROJECT)
public final class WeaveAgentService implements Disposable {
  private static final String AGENT_SERVER_LAUNCHER_MAIN_CLASS = "org.mule.weave.v2.agent.server.AgentServerLauncher";
  public static final int MAX_RETRIES = 10;
  public static final long MAX_ALLOWED_WAIT = 10;

  private final Alarm myRestartAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);

  @Nullable
  private WeaveAgentClient client;
  private ProcessHandler processHandler;
  private boolean disabled = false;
  private List<WeaveAgentStatusListener> listeners;

  private Alarm idleAgentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);

  private static final Logger LOG = Logger.getInstance(WeaveAgentService.class);
  private Project myProject;


  private final AgentClasspathResolver agentClasspathResolver = new ResourceBasedAgentClasspathResolver();

  private WeaveAgentService(Project project) {
    this.myProject = project;
    this.listeners = new ArrayList<>();
    initialize();
  }

  public void addStatusListener(WeaveAgentStatusListener listener) {
    if (client != null && client.isConnected()) {
      listener.agentStarted();
    }
    this.listeners.add(listener);
  }


  public void initialize() {
    myProject.getMessageBus()
            .connect(myProject)
            .subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
              @Override
              public void rootsChanged(@NotNull ModuleRootEvent event) {
                //We stop the server as classpath has changed
                //But only if the process was started
                if (processHandler != null) {
                  scheduleRestart();
                }
              }
            });

    final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    final MessageBusConnection connection = messageBus.connect(this);

    connection.subscribe(BuildManagerListener.TOPIC, new BuildManagerListener() {
      @Override
      public void buildFinished(@NotNull Project project, @NotNull UUID sessionId, boolean isAutomake) {
        if (project == myProject) {
          scheduleRestart();
        }
      }
    });

    connection.subscribe(CompilerTopics.COMPILATION_STATUS, new CompilationStatusListener() {
      @Override
      public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        compilationFinished(compileContext);
      }

      @Override
      public void automakeCompilationFinished(int errors, int warnings, @NotNull CompileContext compileContext) {
        compilationFinished(compileContext);
      }

      private void compilationFinished(@NotNull CompileContext context) {
        if (!(context instanceof DummyCompileContext) && context.getProject() == myProject) {
          scheduleRestart();
        }
      }
    });

    Runtime.getRuntime().addShutdownHook(new Thread(this::tearDown));
  }

  private void scheduleRestart() {
    if (client != null) {
      myRestartAlarm.cancelAllRequests();
      myRestartAlarm.addRequest(() -> {
        //We only restart if there is an active connection
        tearDown();
        init(new EmptyProgressIndicator());
      }, TimeUnit.SECONDS.toMillis(1));

    }
  }


  public void disable() {
    this.disabled = true;
  }

  public void enable() {
    this.disabled = false;
  }


  public synchronized void init(ProgressIndicator indicator) {
    if (isEnabled() && (client == null || !client.isConnected())) {

      if (processHandler != null) {
        tearDown();
      }
      int freePort;
      try {
        freePort = NetUtils.findAvailableSocketPort();
      } catch (IOException e) {
        freePort = 2333;
      }

      LOG.info("DataWeave agent is starting on port " + freePort);

      final ProgramRunner<RunnerSettings> runner = new DefaultProgramRunner() {
        @Override
        @NotNull
        public String getRunnerId() {
          return "Weave Agent Runner";
        }

        @Override
        public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
          return true;
        }

      };
      final Executor executor = DefaultRunExecutor.getRunExecutorInstance();
      try {
        final ProjectRootManager manager = ProjectRootManager.getInstance(myProject);
        if (manager.getProjectSdk() == null) {
          return;
        }
        final RunProfileState state = createRunProfileState(freePort);
        final ExecutionResult result = state.execute(executor, runner);

        //noinspection ConstantConditions
        processHandler = result.getProcessHandler();
        processHandler.addProcessListener(new ProcessAdapter() {

          @Override
          public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
            LOG.info("[Agent Process] " + event.getText());
          }
        });
        int i = 0;
        //Wait for two seconds
        while (!processHandler.waitFor(MAX_ALLOWED_WAIT) && i < 200) {
          i = i + 1;
        }
      } catch (Throwable e) {
        Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Unable to start agent", "Unable to start agent. Reason: \n" + e.getMessage(), NotificationType.ERROR));
        LOG.warn("\"Unable to start agent. Reason: \\n\" + e.getMessage()", e);
        e.printStackTrace();
        disable();
        return;
      }
      processHandler.startNotify();
      TcpClientProtocol clientProtocol = new TcpClientProtocol("localhost", freePort);
      client = new WeaveAgentClient(clientProtocol);

      final int finalFreePort = freePort;
      client.connect(MAX_RETRIES, 1000L, new ConnectionRetriesListener() {
        @Override
        public void failToConnect(String reason) {
          indicator.setText2("Fail to connect to the agent client at port " + finalFreePort + " reason " + reason);
          LOG.warn("Fail to connect to the agent client at port " + finalFreePort + " reason " + reason + " ; will retry in 1 second");
        }

        @Override
        public void connectedSuccessfully() {
          indicator.setText2("Agent connected successfully");
          Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Server started", "Weave Server started and is reachable at port " + finalFreePort, NotificationType.INFORMATION));
          LOG.info("Weave Server started and is reachable at port " + finalFreePort);
        }

        @Override
        public void startConnecting() {
          indicator.setText2("Trying to connect to the agent client at port " + finalFreePort);
          LOG.info("Trying to connect to the agent client at port " + finalFreePort);
        }

        @Override
        public boolean onRetry(int count, int total) {
          return !indicator.isCanceled();
        }
      });
      if (client != null && client.isConnected()) {
        LOG.info("Weave agent connected to server. Port: " + finalFreePort);
        for (WeaveAgentStatusListener listener : listeners) {
          listener.agentStarted();
        }
      } else {
        LOG.warn("WeaveAgentRuntimeManager cannot be started, disabling...");
        //disable the service as for some weird reason it can not be started
        disable();
      }

    }

    if (client != null) {
      actionOccured();
    }

  }

  void actionOccured() {
    idleAgentAlarm.cancelAllRequests();
    idleAgentAlarm.addRequest(() -> {
      if (idleAgentAlarm.isDisposed()) {
        return;
      }
      tearDown();
    }, DataWeaveSettingsState.getInstance().getMaxTimePreview() * 100);
    //If after 5 minute the agent is not used it is going to be teardown to avoid too many running servers
  }

  public void runPreview(String inputsPath, String script, String identifier, String url, Long maxTime, Module module, RunPreviewCallback callback) {
    checkClientConnected(() -> {
      //Make sure all files are persisted before running preview
      ApplicationManager.getApplication().invokeLater(() -> {
        //Save all files
        FileDocumentManager.getInstance().saveAllDocuments();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
          final String[] paths = getClasspath(module);
          if (client != null) {
            long startTime = System.currentTimeMillis();
            client.runPreview(inputsPath, script, identifier, url, maxTime, paths, new PreviewExecutedListener() {
              @Override
              public void onPreviewExecuted(PreviewExecutedEvent result) {
                long duration = System.currentTimeMillis() - startTime;
                ApplicationManager.getApplication().invokeLater(() -> {
                  if (result instanceof PreviewExecutedSuccessfulEvent successfulEvent) {
                    callback.onPreviewSuccessful(successfulEvent, duration);
                  } else if (result instanceof PreviewExecutedFailedEvent) {
                    callback.onPreviewFailed((PreviewExecutedFailedEvent) result);
                  }
                });
              }

              @Override
              public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
                Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'runPreview'",
                        "Unexpected error at 'runPreview' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
              }
            });
          }
        });
      });
    });
  }

  @NotNull
  public String[] getClasspath(Module module) {
    final PathsList pathsList = new PathsList();
    loadClasspathInto(module, pathsList);
    return pathsList.getPathList().toArray(new String[0]);
  }

  public String[] getClasspath(Project project) {
    final PathsList pathsList = new PathsList();
    // All modules to use the same things
    final Module[] modules = ModuleManager.getInstance(project).getModules();
    for (Module module : modules) {
      loadClasspathInto(module, pathsList);
    }
    return pathsList.getPathList().toArray(new String[0]);
  }

  public void loadClasspathInto(Module module, PathsList pathsList) {
    final OrderEnumerator orderEnumerator = OrderEnumerator.orderEntries(module).withoutSdk();
    //We add sources too as we don't compile the we want to have the weave files up to date
    pathsList.addVirtualFiles(orderEnumerator.getSourceRoots());
    pathsList.addVirtualFiles(orderEnumerator.getClassesRoots());
    pathsList.addVirtualFiles(orderEnumerator.getAllLibrariesAndSdkClassesRoots());
  }

  public void checkClientConnected(Runnable onConnected) {
    if (isEnabled()) {
      if (client == null || !client.isConnected()) {
        if (isWeaveRuntimeInstalled()) {
          init(new EmptyProgressIndicator());
        }
      }
      if (client != null && client.isConnected()) {
        onConnected.run();
      } else {
        Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Unable to connect", "Client is not able to connect to runtime", NotificationType.WARNING));
        LOG.warn("Unable to connect; Client is " + client);
      }
    }
  }

  private boolean isEnabled() {
    return !disabled;
  }

  public boolean isWeaveRuntimeInstalled() {
    try {
      GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);
      JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(myProject);
      PsiClass c = ReadAction.compute(() -> psiFacade.findClass(getMarkerClassFQName(), scope));
      LOG.info("Checking for Weave Runtime installation, found PsiClass " + c);
      return c != null;
    } catch (IndexNotReadyException e) {
      //If index is not yes available just try it out
      return true;
    }
  }

  private String getMarkerClassFQName() {
    //The class from the agent runtime
    return "org.mule.weave.v2.runtime.DataWeaveScriptingEngine";
  }

  public void calculateImplicitInputTypes(String inputsPath, ImplicitInputTypesCallback callback) {
    checkClientConnected(() -> {
      //Make sure all files are persisted before running preview
      ApplicationManager.getApplication().invokeLater(() -> {
        FileDocumentManager.getInstance().saveAllDocuments();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
          if (client == null) return;

          client.inferInputsWeaveType(inputsPath, new ImplicitWeaveTypesListener() {
            @Override
            public void onImplicitWeaveTypesCalculated(ImplicitInputTypesEvent result) {
              ApplicationManager.getApplication().invokeLater(() -> {
                callback.onInputsTypesCalculated(result);
              });
            }

            @Override
            public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
              Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'inferInputsWeaveType'",
                      "Unexpected error at 'inferInputsWeaveType' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
            }
          });
        });
      });
    });
  }

  public void calculateWeaveType(String path, InferTypeResultCallback callback) {
    checkClientConnected(() -> {
      //Make sure all files are persisted before running preview
      ApplicationManager.getApplication().invokeLater(() -> {
        if (client != null) {
          client.inferWeaveType(path, new WeaveTypeInferListener() {
            @Override
            public void onWeaveTypeInfer(InferWeaveTypeEvent result) {
              ApplicationManager.getApplication().invokeLater(() -> {
                callback.onType(result);
              });
            }

            @Override
            public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
              Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'inferWeaveType'",
                      "Unexpected error at 'inferWeaveType' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
            }
          });
        }
      });
    });
  }

  public void dataFormats(DataFormatsDefinitionCallback callback) {
    checkClientConnected(() -> {
      if (client != null) {
        client.definedDataFormats(new DataFormatDefinitionListener() {
          @Override
          public void onDataFormatDefinitionCalculated(DataFormatsDefinitionsEvent dfde) {
            ApplicationManager.getApplication().invokeLater(() -> {
              callback.onDataFormatsLoaded(dfde);
            });
          }

          @Override
          public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
            Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'definedDataFormats'",
                    "Unexpected error at 'definedDataFormats' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
          }
        });
      }
    });
  }

  public void availableModules(AvailableModulesCallback callback) {
    if (client != null) {
      client.availableModules(new AvailableModulesListener() {
        @Override
        public void onAvailableModules(AvailableModulesEvent am) {
          ApplicationManager.getApplication().invokeLater(() -> {
            callback.onAvailableModules(am);
          });
        }

        @Override
        public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
          Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'availableModules'",
                  "Unexpected error at 'availableModules' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
        }
      });
    }
  }

  public void resolveModule(String identifier, String loader, Project module, ModuleLoadedCallback callback) {
    final String[] paths = getClasspath(module);
    checkClientConnected(() -> {
      if (client != null) {
        client.resolveModule(identifier, loader, paths, new ModuleLoadedListener() {
          @Override
          public void onModuleLoaded(ModuleResolvedEvent result) {
            ApplicationManager.getApplication().invokeLater(() -> {
              callback.onModuleResolved(result);
            });
          }

          @Override
          public void onUnexpectedError(UnexpectedServerErrorEvent unexpectedServerErrorEvent) {
            Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "[data-weave-agent] Unexpected error at 'resolveModule'",
                    "Unexpected error at 'resolveModule' caused by: \n" + unexpectedServerErrorEvent.stacktrace(), NotificationType.ERROR));
          }
        });
      }
    });
  }

  public synchronized void tearDown() {
    LOG.info("Tearing down WeaveAgent");
    //Kill the process
    if (processHandler != null) {
      processHandler.destroyProcess();
      processHandler = null;
    }

    if (client != null) {
      client.disconnect();
      client = null;
    }
    this.enable();
  }


  private RunProfileState createRunProfileState(int freePort) {
    return new CommandLineState(null) {
      private SimpleJavaParameters createJavaParameters() {
        final SimpleJavaParameters params = WeaveRunnerHelper.createJavaParameters(myProject, AGENT_SERVER_LAUNCHER_MAIN_CLASS);
//        if (Boolean.getBoolean("debugWeaveAgent")) {
//        params.getVMParametersList().add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5678");
//
        for (String jar : agentClasspathResolver.resolveClasspathJars()) {
          params.getClassPath().add(jar);
        }
        ParametersList parametersList = params.getProgramParametersList();
        // parametersList.add("--agent");
        parametersList.add("-p");
        parametersList.add(String.valueOf(freePort));

        // Force use of dynamic classpath.  In Windows, a large project will cause the agent fail to start due
        // to a very large classpath line in the command line.
        //noinspection MissingRecentApi
        params.useDynamicClasspathDefinedByJdkLevel();

        return params;
      }

      @NotNull
      @Override
      public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        return new DefaultExecutionResult(null, processHandler);
      }

      @Override
      @NotNull
      protected OSProcessHandler startProcess() throws ExecutionException {
        SimpleJavaParameters params = createJavaParameters();

        GeneralCommandLine commandLine = params.toCommandLine();
        OSProcessHandler processHandler = new OSProcessHandler(commandLine);
        processHandler.setShouldDestroyProcessRecursively(false);
        return processHandler;
      }
    };
  }

  public static WeaveAgentService getInstance(@NotNull Project project) {
    return project.getService(WeaveAgentService.class);
  }

  @Override
  public void dispose() {
    tearDown();
  }

  public interface WeaveAgentStatusListener {
    void agentStarted();
  }

}
