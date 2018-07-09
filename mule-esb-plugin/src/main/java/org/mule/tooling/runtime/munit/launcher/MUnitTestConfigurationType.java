package org.mule.tooling.runtime.munit.launcher;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.runtime.RuntimeIcons;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;

public class MUnitTestConfigurationType implements ConfigurationType {

  private ConfigurationFactory weaveFactory;

  public MUnitTestConfigurationType() {
    weaveFactory = new MUnitTestConfigurationFactory(this);
  }

  @Override
  public String getDisplayName() {
    return "MUnit Test";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Runs a MUnit test";
  }

  @Override
  public Icon getIcon() {
    return RuntimeIcons.MUnitIcon;
  }

  @NotNull
  @Override
  public String getId() {
    return "org.mule.tooling.runtime.munit.configuration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{weaveFactory};
  }

  @NotNull
  public static MUnitTestConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(MUnitTestConfigurationType.class);
  }

}
