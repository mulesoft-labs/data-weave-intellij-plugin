package org.mule.tooling.runtime.tooling;

import com.intellij.json.psi.JsonElementVisitor;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.mule.tooling.runtime.process.controller.MuleProcessController;
import org.mule.tooling.runtime.process.controller.MuleProcessControllerFactory;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManager;
import org.mule.tooling.runtime.settings.MuleRuntimeSettingsState;
import org.mule.tooling.runtime.util.MuleModuleUtils;
import org.mule.tooling.runtime.util.ResultHolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MUNIT_RUNNER;

public class MuleRuntimeServerManager implements ApplicationComponent {

  private static final int DEFAULT_START_TIMEOUT = 60000;
  private static final int DEFAULT_START_POLL_INTERVAL = 500;
  private static final int DEFAULT_START_POLL_DELAY = 300;
  private static final int DEFAULT_CONTROLLER_OPERATION_TIMEOUT = 15000;
  public static final String MULE_VERSION = "mule.version";
  public static final String MUNIT_VERSION = "munit.version";

  private Map<String, MuleStandaloneController> muleRuntimes;


  public static MuleRuntimeServerManager getInstance() {
    return ApplicationManager.getApplication().getComponent(MuleRuntimeServerManager.class);
  }

  public static String getMuleVersionOf(Module module) {
    String muleVersion = null;
    VirtualFile muleArtifactJson = MuleModuleUtils.getMuleArtifactJson(module);
    if (muleArtifactJson != null) {
      muleVersion = selectMinMuleVersion(module.getProject(), muleArtifactJson);
    }
    if (muleVersion == null) {
      MavenProject mavenProject = MuleModuleUtils.getMavenProject(module);
      if (mavenProject != null) {
        muleVersion = mavenProject.getProperties().getProperty(MULE_VERSION);
      }
    }
    if (muleVersion != null) {
      return muleVersion;
    }
    return MuleRuntimeSettingsState.getInstance().getDefaultRuntimeVersion();
  }

  @Nullable
  public static String getMunitVersionOf(Project project) {
    return project instanceof MavenProject ? getMunitVersionOf(project) : null;
  }

  @Nullable
  public static String getMunitVersionOf(Module module) {
    return getMunitVersionOf(MuleModuleUtils.getMavenProject(module));
  }

  @Nullable
  public static String getMunitVersionOf(MavenProject mavenProject) {
    if (mavenProject != null) {
      String munitVersion = mavenProject.getProperties().getProperty(MUNIT_VERSION);
      if (munitVersion == null) {
        munitVersion = mavenProject.getDependencies().stream()
            .filter(d -> d.getArtifactId().equals(MUNIT_RUNNER))
            .map(MavenArtifact::getVersion)
            .findAny().orElse(null);
      }
      if (munitVersion != null) {
        return munitVersion;
      }
    }
    return null;
  }

  private static String selectMinMuleVersion(Project project, VirtualFile muleArtifactJson) {
    String muleVersion = null;
    PsiFile file = PsiManager.getInstance(project).findFile(muleArtifactJson);
    if (file instanceof JsonFile) {
      PsiElement[] children = file.getChildren();
      if (children.length > 0) {
        ResultHolder<JsonProperty> jsonPropertyResult = new ResultHolder<>();
        children[0].accept(new JsonElementVisitor() {
          @Override
          public void visitObject(@NotNull JsonObject o) {
            JsonProperty minMuleVersion = o.findProperty("minMuleVersion");
            jsonPropertyResult.setResult(minMuleVersion);
          }
        });
        JsonProperty result = jsonPropertyResult.getResult();
        if (result != null && result.getValue() instanceof JsonStringLiteral) {
          muleVersion = ((JsonStringLiteral) result.getValue()).getValue();
        }
      }
    }
    return muleVersion;
  }

  public static String getMuleVersionOf(Project project) {
    String muleVersion = null;
    VirtualFile muleArtifactJson = MuleModuleUtils.getMuleArtifactJson(project);
    if (muleArtifactJson != null) {
      muleVersion = selectMinMuleVersion(project, muleArtifactJson);
    }
    if (muleVersion == null) {
      MavenProject mavenProject = MuleModuleUtils.getMavenProject(project);
      if (mavenProject != null) {
        muleVersion = mavenProject.getProperties().getProperty(MULE_VERSION);
      }
    }
    if (muleVersion != null) {
      return muleVersion;
    } else {
      return MuleRuntimeSettingsState.getInstance().getDefaultRuntimeVersion();
    }
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
        final MuleStandaloneController muleStandaloneController = new MuleStandaloneController(processController, statusChecker);
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
