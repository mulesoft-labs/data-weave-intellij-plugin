package org.mule.tooling.runtime.munit.launcher;


import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;

public class MUnitTestConfiguration extends ModuleBasedConfiguration
    implements ModuleRunProfile, RunConfigurationWithSuppressedDefaultDebugAction {

  public static final String PREFIX = "MUnit-";

  private Project project;

  protected MUnitTestConfiguration(String name, @NotNull ConfigurationFactory factory, Project project) {
    super(name, new JavaRunConfigurationModule(project, true), factory);
    this.project = project;
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new MUnitTestRunnerEditor(this);
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
    return new MUnitTestRunnerCommandLine(executionEnvironment);
  }

  @Override
  public Collection<Module> getValidModules() {
    final ModuleManager moduleManager = ModuleManager.getInstance(this.project);
    return Arrays.asList(moduleManager.getModules());
  }


  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    super.checkConfiguration();
  }

  public Module getModule() {
    return getConfigurationModule().getModule();
  }
}
