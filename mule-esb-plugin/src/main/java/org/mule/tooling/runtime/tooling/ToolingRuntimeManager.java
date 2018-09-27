package org.mule.tooling.runtime.tooling;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ClassLoaderUtil;
import com.intellij.openapi.util.Computable;
import com.intellij.util.Producer;
import com.intellij.util.concurrency.FutureResult;
import org.jetbrains.annotations.NotNull;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.tooling.client.api.ToolingRuntimeClient;
import org.mule.tooling.client.api.configuration.agent.AgentConfiguration;
import org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrap;
import org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrapConfiguration;
import org.mule.tooling.runtime.util.MavenConfigurationUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrapFactory.newToolingRuntimeClientBootstrap;

/**
 * Manages the tooling runtime Instances
 */
public class ToolingRuntimeManager implements ApplicationComponent {

  public static final int POLLING_FREQUENCY = 100;
  public static final int MAX_TIMEOUT = 500;

  public static final String DEFAULT_TOOLING_CLIENT_FOR_40 = "4.0.2";

  private Map<String, ToolingRuntimeClient> toolingRuntimeByVersion;
  private volatile boolean isStarting;

  public ToolingRuntimeManager() {
    this.isStarting = false;
    this.toolingRuntimeByVersion = new ConcurrentHashMap<>();
  }


  public <T> T callOnToolingRuntime(Project project, String runtimeVersion, Function<ToolingRuntimeClient, T> caller, Producer<T> defaultValue) {
    if (toolingRuntimeByVersion.containsKey(runtimeVersion)) {
      return caller.apply(toolingRuntimeByVersion.get(runtimeVersion));
    } else {
      synchronized (this) {
        if (toolingRuntimeByVersion.containsKey(runtimeVersion)) {
          return caller.apply(toolingRuntimeByVersion.get(runtimeVersion));
        } else {
          FutureResult<T> result = new FutureResult<>();
          if (isStarting) {
            int i = 0;
            while (i < MAX_TIMEOUT / POLLING_FREQUENCY) {
              i = i + 1;
              if (toolingRuntimeByVersion.containsKey(runtimeVersion)) {
                return caller.apply(toolingRuntimeByVersion.get(runtimeVersion));
              }
              try {
                Thread.sleep(POLLING_FREQUENCY);
              } catch (InterruptedException e) {
                return defaultValue.produce();
              }
            }
            return defaultValue.produce();
          } else {
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Initializing Tooling Runtime", true) {
              @Override
              public void run(@NotNull ProgressIndicator indicator) {
                isStarting = true;
                ToolingRuntimeClient runtimeClient = initRuntimeClient(project, runtimeVersion, getToolingVersionForRuntime(runtimeVersion), indicator);
                isStarting = false;
                if (runtimeClient != null)
                    result.set(caller.apply(runtimeClient));
              }
            });
            try {
              return result.get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
              return defaultValue.produce();
            }
          }
        }
      }
    }

  }

  private String getToolingVersionForRuntime(String runtimeVersion) {
    if (runtimeVersion.startsWith("4.0")) {
      return DEFAULT_TOOLING_CLIENT_FOR_40;
    }
    return runtimeVersion;
  }

  //@NotNull
  private ToolingRuntimeClient initRuntimeClient(Project project, String runtimeVersion, String toolingVersion, ProgressIndicator progressIndicator) {
    progressIndicator.setText("Initializing Tooling Runtime");
    final MavenConfiguration mavenConfiguration = MavenConfigurationUtils.createMavenConfiguration();
    final ToolingRuntimeClientBootstrap toolingClientBootstrap = createToolingClientBootstrap(runtimeVersion, toolingVersion, mavenConfiguration, progressIndicator);
    final AgentConfiguration.Builder builder = AgentConfiguration.builder();
    final MuleRuntimeStatusChecker runtime = MuleRuntimeServerManager.getInstance().getMuleRuntime(project, runtimeVersion, progressIndicator);
    if (runtime != null) {
      //There may be no runtime
      builder.withToolingApiUrl(runtime.getToolingApiUrl());
    }
    final AgentConfiguration build = builder.build();
    if (toolingClientBootstrap != null) {
      final ToolingRuntimeClient toolingRuntimeClient = createToolingRuntimeClient(toolingClientBootstrap, mavenConfiguration, build, progressIndicator);
      toolingRuntimeByVersion.put(runtimeVersion, toolingRuntimeClient);
      //Notify that a tooling runtime is available
      ApplicationManager.getApplication().getMessageBus().syncPublisher(ToolingRuntimeTopics.TOOLING_STARTED).onToolingRuntimeStarted(runtimeVersion);
      progressIndicator.setText("Tooling Runtime Started successfully");
      return toolingRuntimeClient;
    } else {
      progressIndicator.setText("Unable to start Tooling Runtime");
      return null;
    }
  }

  public static ToolingRuntimeManager getInstance() {
    return ApplicationManager.getApplication().getComponent(ToolingRuntimeManager.class);
  }

  public ToolingRuntimeClientBootstrap createToolingClientBootstrap(String muleVersion, String toolingVersion, MavenConfiguration mavenConfiguration, ProgressIndicator progressIndicator) {

    final ToolingRuntimeClientBootstrapConfiguration toolingRuntimeClientBootstrapConfiguration = ToolingRuntimeClientBootstrapConfiguration.builder()
        .muleVersion(muleVersion)
        .toolingVersion(toolingVersion)
        .mavenConfiguration(mavenConfiguration)
        .build();

    progressIndicator.setText2("Initializing Tooling Bootstrap");

    try {
      final ToolingRuntimeClientBootstrap toolingRuntimeClientBootstrap = ClassLoaderUtil.runWithClassLoader(getClass().getClassLoader(), (Computable<ToolingRuntimeClientBootstrap>) () -> newToolingRuntimeClientBootstrap(toolingRuntimeClientBootstrapConfiguration));
      progressIndicator.setText2("Tooling Bootstrap Initialized");
      return toolingRuntimeClientBootstrap;
    } catch (Throwable e) {
      progressIndicator.setText2("Unable to initialize Tooling Bootstrap : " + e.getMessage());
      return null;
    }
  }

  public ToolingRuntimeClient createToolingRuntimeClient(ToolingRuntimeClientBootstrap toolingRuntimeClientBootstrap,
                                                         MavenConfiguration mavenConfiguration,
                                                         AgentConfiguration agentConfiguration,
                                                         ProgressIndicator progressIndicator) {
    progressIndicator.setText2("Creating Tooling Runtime Client");
    final ToolingRuntimeClient.Builder builder = toolingRuntimeClientBootstrap.getToolingRuntimeClientBuilderFactory().create();
    final ToolingRuntimeClient build = builder
        .withMavenConfiguration(mavenConfiguration)
        .withRemoteAgentConfiguration(agentConfiguration)
        .build();
    progressIndicator.setText2("Tooling Runtime Created");
    return build;
  }
}
