package org.mule.tooling.lang.dw.launcher.configuration;

import com.intellij.execution.configurations.RunConfiguration;

public interface WeaveBasedConfiguration extends RunConfiguration {
    String getWorkingDirectory();
}
