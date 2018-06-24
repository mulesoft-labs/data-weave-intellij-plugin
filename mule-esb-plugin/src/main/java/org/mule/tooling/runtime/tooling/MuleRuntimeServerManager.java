package org.mule.tooling.runtime.tooling;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.process.controller.MuleProcessController;
import org.mule.tooling.runtime.process.controller.MuleProcessControllerFactory;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManager;
import org.mule.tooling.runtime.settings.MuleRuntimeSettingsState;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MuleRuntimeServerManager implements ApplicationComponent {

  private static final int DEFAULT_START_TIMEOUT = 60000;
  private static final int DEFAULT_START_POLL_INTERVAL = 500;
  private static final int DEFAULT_START_POLL_DELAY = 300;
  private static final int DEFAULT_CONTROLLER_OPERATION_TIMEOUT = 15000;

  private Map<String, MuleStandaloneController> muleRuntimes;


  public static MuleRuntimeServerManager getInstance() {
    return ApplicationManager.getApplication().getComponent(MuleRuntimeServerManager.class);
  }

  public static String getMuleVersionOf(Module module) {
    return MuleRuntimeSettingsState.getInstance().getDefaultRuntimeVersion();
  }

  public static String getMuleVersionOf(Project project) {
    return MuleRuntimeSettingsState.getInstance().getDefaultRuntimeVersion();
  }

  @Override
  public void initComponent() {
    this.muleRuntimes = new HashMap<>();
  }

  @Override
  public void disposeComponent() {
    for (MuleStandaloneController value: muleRuntimes.values()) {
      value.stop(new EmptyProgressIndicator());
    }
  }

  @Nullable
  public synchronized MuleRuntimeStatusChecker getMuleRuntime(Project project, String version, ProgressIndicator progressIndicator) {
    if (muleRuntimes.containsKey(version)) {
      MuleStandaloneController muleStandaloneController = muleRuntimes.get(version);
      if (muleStandaloneController.isInitializing() || (muleStandaloneController.isRunning() && muleStandaloneController.getChecker().isRunning())) {
        return muleStandaloneController.getChecker();
      } else {
        //If process ir running but is not responding
        if (muleStandaloneController.isRunning()) {
          ProgressManager.getInstance().run(new Task.Backgroundable(project, "Restarting Mule Runtime", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
              muleStandaloneController.stop(progressIndicator);
              muleStandaloneController.start(progressIndicator);
            }
          });

        }
        return muleStandaloneController.getChecker();
      }
    } else {
      final MuleSdk sdkByVersion = MuleSdkManager.getInstance().getSdkByVersion(version);
      if (sdkByVersion != null) {
        final String muleDir = sdkByVersion.getMuleHome();
        final File file = new File(muleDir);
        final MuleProcessController processController = MuleProcessControllerFactory.createController(file, DEFAULT_CONTROLLER_OPERATION_TIMEOUT);
        final MuleRuntimeStatusChecker statusChecker = new MuleRuntimeStatusChecker(processController, new MuleAgentConfiguration(
            "http",
            freePort(),
            DEFAULT_START_TIMEOUT,
            DEFAULT_START_POLL_INTERVAL,
            DEFAULT_START_POLL_DELAY));
        final MuleStandaloneController muleStandaloneController = new MuleStandaloneController(version, processController, statusChecker);
        muleRuntimes.put(version, muleStandaloneController);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Initializing Mule Runtime", true) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            muleStandaloneController.start(progressIndicator);
          }

        });
        return statusChecker;
      } else {
        return null;
      }

    }
  }

  private int freePort() {
    int freePort;
    try {
      freePort = NetUtils.findAvailableSocketPort();
    } catch (IOException e) {
      freePort = (int) (Math.random() * 9999);
    }
    return freePort;
  }


}
