package org.mule.tooling.runtime.launcher.configuration;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;


class MuleConfigurationFactory extends ConfigurationFactory {

  public MuleConfigurationFactory(MuleConfigurationType configurationType) {
    super(configurationType);
  }

  @NotNull
  public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new MuleConfiguration("Mule Runtime", this, project);
  }

  @Override
  public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
    super.configureBeforeRunTaskDefaults(providerID, task);
  }
}
