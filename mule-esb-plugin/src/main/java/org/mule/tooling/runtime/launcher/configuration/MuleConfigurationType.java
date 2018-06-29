package org.mule.tooling.runtime.launcher.configuration;


import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.runtime.RuntimeIcons;

import javax.swing.*;

public class MuleConfigurationType implements ConfigurationType {

  private ConfigurationFactory esbFactory;

  public MuleConfigurationType() {
    esbFactory = new MuleConfigurationFactory(this);
  }

  @Override
  public String getDisplayName() {
    return "Mule Runtime";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Mule Runtime";
  }

  @Override
  public Icon getIcon() {
    return RuntimeIcons.MuleRunConfigIcon;
  }

  @NotNull
  @Override
  public String getId() {
    return "org.mule.esb.configuration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{esbFactory};
  }

  @NotNull
  public static MuleConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(MuleConfigurationType.class);
  }

}
