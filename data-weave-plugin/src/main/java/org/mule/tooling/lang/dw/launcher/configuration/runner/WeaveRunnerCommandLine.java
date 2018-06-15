package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveConfiguration;
import org.mule.tooling.lang.dw.launcher.configuration.ui.WeaveInput;

import java.util.List;

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

    final List<WeaveInput> weaveInputs = model.getWeaveInputs();
    for (WeaveInput weaveInput : weaveInputs) {
      javaParams.getProgramParametersList().add("-input");
      javaParams.getProgramParametersList().add(weaveInput.getName());
      javaParams.getProgramParametersList().add(weaveInput.getPath());
    }

    if (isDebug) {
      javaParams.getVMParametersList().add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005");
      javaParams.getProgramParametersList().add("-debug");
    }

    if (!StringUtils.isBlank(model.getWeaveOutput())) {
      javaParams.getProgramParametersList().add("-output", model.getWeaveOutput());
    }

    javaParams.getProgramParametersList().add(model.getWeaveFile());

    // All done, run it
    return javaParams;
  }

  public WeaveConfiguration getModel() {
    return model;
  }

}
