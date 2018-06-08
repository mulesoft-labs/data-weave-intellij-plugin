package org.mule.tooling.lang.dw.service.agent;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PathsList;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveRunnerHelper;
import org.mule.weave.v2.debugger.client.ConnectionRetriesListener;
import org.mule.weave.v2.debugger.client.DefaultWeaveAgentClientListener;
import org.mule.weave.v2.debugger.client.WeaveAgentClient;
import org.mule.weave.v2.debugger.client.tcp.TcpClientProtocol;
import org.mule.weave.v2.debugger.event.AvailableModulesEvent;
import org.mule.weave.v2.debugger.event.DataFormatsDefinitionsEvent;
import org.mule.weave.v2.debugger.event.ImplicitInputTypesEvent;
import org.mule.weave.v2.debugger.event.InferWeaveTypeEvent;
import org.mule.weave.v2.debugger.event.ModuleResolvedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import java.io.IOException;

public class WeaveAgentComponent extends AbstractProjectComponent {


    public static final int MAX_RETRIES = 10;
    public static final long ONE_SECOND_DELAY = 1000L;

    @Nullable
    private WeaveAgentClient client;
    private ProcessHandler processHandler;
    private boolean disabled = false;

    protected WeaveAgentComponent(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        DumbService.getInstance(myProject).runWithAlternativeResolveEnabled(() -> {
            if (isWeaveRuntimeInstalled()) {
                //We initialized if it is installed
                ProgressManager.getInstance().run(new Task.Backgroundable(myProject, "Initializing Weave Agent", true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        init(indicator);
                    }

                });
            }
        });

    }

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }

    @Override
    public void projectClosed() {
        super.projectClosed();
        tearDown();
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
            final ProgramRunner runner = new DefaultProgramRunner() {
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
                final RunProfileState state = createRunProfileState(freePort);
                final ExecutionResult result = state.execute(executor, runner);
                //noinspection ConstantConditions
                processHandler = result.getProcessHandler();
                //Wait for it to init
                processHandler.waitFor(ONE_SECOND_DELAY);
            } catch (Throwable e) {
                Notifications.Bus.notify(new Notification("Data Weave", "Unable to start agent", "Unable to start agent. Reason: \n" + e.getMessage(), NotificationType.ERROR));
                e.printStackTrace();
                disable();
                return;
            }
            processHandler.startNotify();
            TcpClientProtocol clientProtocol = new TcpClientProtocol("localhost", freePort);
            client = new WeaveAgentClient(clientProtocol, new DefaultWeaveAgentClientListener());

            final int finalFreePort = freePort;
            client.connect(MAX_RETRIES, 1000L, new ConnectionRetriesListener() {
                @Override
                public void failToConnect(String reason) {
                    indicator.setText2("Fail to connect to the agent client at port " + finalFreePort + " reason " + reason);
                }

                @Override
                public void connectedSuccessfully() {
                    indicator.setText2("Agent connected successfully");
                }

                @Override
                public void startConnecting() {
                    indicator.setText2("Trying to connect to the agent client at port " + finalFreePort);
                }

                @Override
                public boolean onRetry(int count, int total) {
                    return !indicator.isCanceled();
                }
            });
            if (client.isConnected()) {
                System.out.println("Weave agent connected to server. Port: " + finalFreePort);
            } else {
                //disable the service as for some weird reason it can not be started
                disable();
            }

        }

    }

    public void runPreview(String inputsPath, String script, String identifier, String url, Long maxTime, Module module, RunPreviewCallback callback) {
        checkClientConnected(() -> {
            //Make sure all files are persisted before running preview
            ApplicationManager.getApplication().invokeLater(() -> {
                //Save all files
                FileDocumentManager.getInstance().saveAllDocuments();
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    final String[] paths = getClasspath(module);
                    client.runPreview(inputsPath, script, identifier, url, maxTime, paths, new DefaultWeaveAgentClientListener() {
                        @Override
                        public void onPreviewExecuted(PreviewExecutedEvent result) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                if (result instanceof PreviewExecutedSuccessfulEvent) {
                                    PreviewExecutedSuccessfulEvent successfulEvent = (PreviewExecutedSuccessfulEvent) result;
                                    callback.onPreviewSuccessful(successfulEvent);
                                } else if (result instanceof PreviewExecutedFailedEvent) {
                                    callback.onPreviewFailed((PreviewExecutedFailedEvent) result);
                                }

                            });
                        }
                    });
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
        if (modules.length > 0) {
            for (Module module : modules) {
                loadClasspathInto(module, pathsList);
            }
        }
        return pathsList.getPathList().toArray(new String[0]);
    }

    public void loadClasspathInto(Module module, PathsList pathsList) {
        final OrderEnumerator orderEnumerator = OrderEnumerator.orderEntries(module).withoutLibraries().withoutSdk();
        //We add sources too as we don't compile the we want to have the weave files up to date
        pathsList.addVirtualFiles(orderEnumerator.getSourceRoots());
        pathsList.addVirtualFiles(orderEnumerator.getClassesRoots());
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
                Notifications.Bus.notify(new Notification("Data Weave", "Unable to connect", "Client is not able to connect to runtime", NotificationType.WARNING));
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
            return c != null;
        } catch (IndexNotReadyException e) {
            //If index is not yes available just try it out
            return true;
        }
    }

    private String getMarkerClassFQName() {
        //The class from the agent runtime
        return "org.mule.weave.v2.runtime.utils.AgentCustomRunner";
    }


    public void calculateImplicitInputTypes(String inputsPath, ImplicitInputTypesCallback callback) {
        checkClientConnected(() -> {
            //Make sure all files are persisted before running preview
            ApplicationManager.getApplication().invokeLater(() -> {
                FileDocumentManager.getInstance().saveAllDocuments();
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    client.inferInputsWeaveType(inputsPath, new DefaultWeaveAgentClientListener() {
                        @Override
                        public void onImplicitWeaveTypesCalculated(ImplicitInputTypesEvent result) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                callback.onInputsTypesCalculated(result);
                            });
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
                client.inferWeaveType(path, new DefaultWeaveAgentClientListener() {
                    @Override
                    public void onWeaveTypeInfer(InferWeaveTypeEvent result) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            callback.onType(result);
                        });
                    }
                });
            });
        });
    }

    public void dataFormats(DataFormatsDefinitionCallback callback) {
        checkClientConnected(() -> {
            client.definedDataFormats(new DefaultWeaveAgentClientListener() {
                @Override
                public void onDataFormatDefinitionCalculated(DataFormatsDefinitionsEvent dfde) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        callback.onDataFormatsLoaded(dfde);
                    });
                }
            });
        });
    }

    public void availableModules(AvailableModulesCallback callback) {
        client.availableModules(new DefaultWeaveAgentClientListener() {
            @Override
            public void onAvailableModules(AvailableModulesEvent am) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    callback.onAvailableModules(am);
                });
            }
        });
    }

    public void resolveModule(String identifier, String loader, Project module, ModuleLoadedCallback callback) {
        final String[] paths = getClasspath(module);
        checkClientConnected(() -> {
            client.resolveModule(identifier, loader, paths, new DefaultWeaveAgentClientListener() {
                @Override
                public void onModuleLoaded(ModuleResolvedEvent result) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        callback.onModuleResolved(result);
                    });
                }
            });
        });
    }

    public void tearDown() {
        //Kill the process
        if (processHandler != null) {
            processHandler.destroyProcess();
            processHandler = null;
        }

        if (client != null) {
            client.disconnect();
            client = null;
        }
    }


    private RunProfileState createRunProfileState(int freePort) {
        return new CommandLineState(null) {
            private SimpleJavaParameters createJavaParameters() {
                final SimpleJavaParameters params = WeaveRunnerHelper.createJavaParameters(myProject);
                if (Boolean.getBoolean("debugWeaveAgent")) {
                    params.getVMParametersList().add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5678");
                }
                ParametersList parametersList = params.getProgramParametersList();
                parametersList.add("--agent");
                parametersList.add("-p");
                parametersList.add(String.valueOf(freePort));
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

    public static WeaveAgentComponent getInstance(@NotNull Project project) {
        return project.getComponent(WeaveAgentComponent.class);
    }

}
