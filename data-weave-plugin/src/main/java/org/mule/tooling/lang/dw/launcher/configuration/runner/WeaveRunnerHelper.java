package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.settings.DataWeaveSettingsState;

public class WeaveRunnerHelper {
  public static final String WEAVE_RUNNER_MAIN_CLASS = "org.mule.weave.v2.runtime.utils.WeaveRunner";

  @NotNull
  public static JavaParameters createJavaParameters(Project project) {
    return createJavaParameters(project, WEAVE_RUNNER_MAIN_CLASS);
  }

  public static JavaParameters createJavaParameters(Project project, String mainClass) {
    final JavaParameters javaParams = new JavaParameters();
    final ProjectRootManager manager = ProjectRootManager.getInstance(project);
    javaParams.setJdk(manager.getProjectSdk());
    // All modules to use the same things
    final Module[] modules = ModuleManager.getInstance(project).getModules();
    for (Module module : modules) {
      ApplicationManager.getApplication().runReadAction(() -> {
        try {
          javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);
        } catch (CantRunException ignored) {
        }
      });
    }
    javaParams.setMainClass(mainClass);
    setupDefaultVMParams(javaParams);
    return javaParams;
  }

  public static void setupDefaultVMParams(JavaParameters javaParams) {
    //Add default vm parameters
    String jvmParameters = DataWeaveSettingsState.getInstance().getJvmParameters();
    String[] args = jvmParameters.split("\\s+");
    for (String arg : args) {
      javaParams.getVMParametersList().add(arg);
    }

  }
}
