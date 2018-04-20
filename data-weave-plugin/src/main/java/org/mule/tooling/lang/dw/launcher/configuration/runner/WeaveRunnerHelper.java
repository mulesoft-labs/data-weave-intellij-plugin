package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

public class WeaveRunnerHelper {
    public static final String WEAVE_RUNNER_MAIN_CLASS = "org.mule.weave.v2.runtime.utils.WeaveRunner";

    @NotNull
    public static JavaParameters createJavaParameters(Project project) {
        final JavaParameters javaParams = new JavaParameters();
        final ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        // All modules to use the same things
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 0) {
            for (Module module : modules) {
                try {
                    javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);
                } catch (CantRunException e) {
                }
            }
        }
        javaParams.setMainClass(WEAVE_RUNNER_MAIN_CLASS);
        setupDefaultVMParams(javaParams);
        return javaParams;
    }

    public static void setupDefaultVMParams(JavaParameters javaParams) {
        //Add default vm parameters
        javaParams.getVMParametersList().add("-Xms1024m");
        javaParams.getVMParametersList().add("-Xmx1024m");
        javaParams.getVMParametersList().add("-XX:+HeapDumpOnOutOfMemoryError");
        javaParams.getVMParametersList().add("-XX:+AlwaysPreTouch");
        javaParams.getVMParametersList().add("-XX:NewSize=512m");
        javaParams.getVMParametersList().add("-XX:MaxNewSize=512m");
        javaParams.getVMParametersList().add("-XX:MaxTenuringThreshold=8");
    }
}
