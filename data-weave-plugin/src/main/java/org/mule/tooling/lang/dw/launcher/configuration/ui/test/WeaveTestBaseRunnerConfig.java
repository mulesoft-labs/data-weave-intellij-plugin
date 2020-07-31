package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveBasedConfiguration;

import java.util.List;

public interface WeaveTestBaseRunnerConfig extends WeaveBasedConfiguration {
    Module getModule();

    List<String> getTests();

    String getTestToRun();

    default boolean isUpdateResult() {
        return false;
    }

    default void addAdditionalVMParameters(JavaParameters javaParams) {
    }
}
