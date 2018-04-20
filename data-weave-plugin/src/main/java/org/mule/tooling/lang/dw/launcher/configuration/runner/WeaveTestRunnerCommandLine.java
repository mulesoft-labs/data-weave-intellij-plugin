package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveTestConfiguration;

public class WeaveTestRunnerCommandLine extends JavaCommandLineState {

    //Mule Main Class

    private final boolean isDebug;
    private WeaveTestConfiguration configuration;

    public WeaveTestRunnerCommandLine(@NotNull ExecutionEnvironment environment, WeaveTestConfiguration configuration) {
        super(environment);
        this.isDebug = DefaultDebugExecutor.EXECUTOR_ID.equals(environment.getExecutor().getId());
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters javaParams = new JavaParameters();
        // Use the same JDK as the project
        final Module module = this.configuration.getModule();
        final ProjectRootManager manager = ProjectRootManager.getInstance(module.getProject());
        javaParams.setJdk(manager.getProjectSdk());
        // All modules to use the same things
        javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);
        javaParams.setMainClass(WeaveRunnerHelper.WEAVE_RUNNER_MAIN_CLASS);

        //Add default vm parameters
        WeaveRunnerHelper.setupDefaultVMParams(javaParams);

        ParametersList params = javaParams.getProgramParametersList();
        params.add("--wtest");
        params.add("-testlistener");
        params.add("intellij");

        if (isDebug) {
            javaParams.getVMParametersList().add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005");
            params.add("-debug");
        }

        params.add("-test");
        params.add(configuration.getWeaveFile());

        // All done, run it
        return javaParams;
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        RunConfiguration runConfiguration = getConfiguration();
        TestConsoleProperties properties = new SMTRunnerConsoleProperties(runConfiguration, "WeaveTest", executor);
        ConsoleView console = SMTestRunnerConnectionUtil.createAndAttachConsole("WeaveTest", processHandler, properties);
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
    }

    public WeaveTestConfiguration getConfiguration() {
        return configuration;
    }
}
