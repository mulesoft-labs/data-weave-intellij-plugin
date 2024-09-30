package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveConfiguration;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WeaveRunnerCommandLine extends WeaveCommandLineState {

    //Mule Main Class

    private final boolean isDebug;
    private WeaveConfiguration model;

    public WeaveRunnerCommandLine(@NotNull ExecutionEnvironment environment, WeaveConfiguration model) {
        super(environment);
        this.isDebug = DefaultDebugExecutor.EXECUTOR_ID.equals(environment.getExecutor().getId());
        this.model = model;
    }

    @Override
    protected JavaParameters createJavaParameters() {
        // Use the same JDK as the project
        final Project project = this.model.getProject();

        final JavaParameters javaParams = WeaveRunnerHelper.createJavaParameters(project);
        if (isDebug) {
            javaParams.getProgramParametersList().add("-debug");
        }
        final String scenario = model.getScenario();
        if (!isBlank(scenario)) {
            VirtualFile resolve = VirtualFileSystemUtils.resolve(model.getModule(), NameIdentifier.apply(model.getNameIdentifier(), Option.<String>empty()));
            if (resolve != null) {
                final WeaveRuntimeService instance = WeaveRuntimeService.getInstance(project);
                Scenario scenarioWithName = instance.getScenarioWithName(resolve, scenario);
                if (scenarioWithName != null && scenarioWithName.getInputs() != null) {
                    javaParams.getProgramParametersList().add("-scenario", scenarioWithName.getInputs().getPath());
                }
            }
        }

        //Set user.dir to module home
        final String workingDirectory = model.getWorkingDirectory();
        if (isNotBlank(workingDirectory)) {
            javaParams.getVMParametersList().addProperty("user.dir", workingDirectory);
        }

        if (isNotBlank(model.getOutputPath())) {
            javaParams.getProgramParametersList().add("-output", model.getOutputPath());
        }

        javaParams.getProgramParametersList().add(model.getNameIdentifier());

        // All done, run it
        return javaParams;
    }

    public WeaveConfiguration getModel() {
        return model;
    }

}
