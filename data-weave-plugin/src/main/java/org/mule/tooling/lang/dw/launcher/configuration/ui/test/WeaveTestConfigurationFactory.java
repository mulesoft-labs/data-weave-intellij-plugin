package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class WeaveTestConfigurationFactory extends ConfigurationFactory{
    protected WeaveTestConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new WeaveTestConfiguration("Weave Test", this, project);
    }
}
