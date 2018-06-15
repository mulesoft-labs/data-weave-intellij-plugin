package org.mule.tooling.lang.dw.launcher.configuration.runner;

import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

public abstract class WeaveCommandLineState extends JavaCommandLineState {

    protected WeaveCommandLineState(@NotNull ExecutionEnvironment environment) {
        super(environment);
    }
}
