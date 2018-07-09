package org.mule.tooling.runtime.munit.launcher;

import org.jetbrains.annotations.NotNull;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class MUnitTestConfigurationFactory extends ConfigurationFactory {
  protected MUnitTestConfigurationFactory(@NotNull ConfigurationType type) {
    super(type);
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new MUnitTestConfiguration("MUnit Test", this, project);
  }
}
