package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveBasedConfiguration;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface WeaveTestBaseRunnerConfig extends WeaveBasedConfiguration {
    Module getModule();

    List<String> getTests();

    String getTestToRun();

    default boolean isUpdateResult() {
        return false;
    }

    String getVmOptions();

    default void addAdditionalVMParameters(JavaParameters javaParams) {
        final String vmOptions = getVmOptions();
        if (isNotBlank(vmOptions))
            javaParams.getVMParametersList().add(vmOptions);
    }

}
