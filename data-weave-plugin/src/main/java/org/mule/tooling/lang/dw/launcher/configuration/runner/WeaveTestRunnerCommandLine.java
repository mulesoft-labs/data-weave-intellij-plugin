package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.ui.test.WeaveTestBaseRunnerConfig;
import org.mule.tooling.lang.dw.testintegration.WeaveTestFramework;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WeaveTestRunnerCommandLine extends WeaveCommandLineState {
    public static final String TEST_RUNNER_CLASS_NAME = "org.mule.weave.v2.module.test.runner.TestRunner";
    private final boolean isDebug;
    private WeaveTestBaseRunnerConfig configuration;

    public WeaveTestRunnerCommandLine(@NotNull ExecutionEnvironment environment, WeaveTestBaseRunnerConfig configuration) {
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
        javaParams.setMainClass(TEST_RUNNER_CLASS_NAME);

        //Add default vm parameters
        WeaveRunnerHelper.setupDefaultVMParams(javaParams);
        if (configuration.isUpdateResult()) {
            javaParams.getVMParametersList().addProperty("updateResult", "true");
        }

        if (isNotBlank(configuration.getTestToRun())) {
            javaParams.getVMParametersList().addProperty("testToRun", configuration.getTestToRun());
        }


        configuration.addAdditionalVMParameters(javaParams);
        //Set user.dir to module home

        final String workingDirectory = configuration.getWorkingDirectory();
        if (StringUtils.isNotBlank(workingDirectory)) {
            javaParams.getVMParametersList().addProperty("user.dir", workingDirectory);
        }

        ParametersList params = javaParams.getProgramParametersList();
        params.add("-testlistener");
        params.add("intellij");

        if (isDebug) {
            params.add("-debug");
        }

        final List<String> tests = configuration.getTests();
        for (String test : tests) {
            params.add("-test");
            params.add(test);
        }

        // All done, run it
        return javaParams;
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        RunConfiguration runConfiguration = getConfiguration();
        TestConsoleProperties properties = new SMTRunnerConsoleProperties(runConfiguration, WeaveTestFramework.WEAVE_TEST, executor);
        ConsoleView console = SMTestRunnerConnectionUtil.createAndAttachConsole(WeaveTestFramework.WEAVE_TEST, processHandler, properties);
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
    }

    public WeaveTestBaseRunnerConfig getConfiguration() {
        return configuration;
    }

}
