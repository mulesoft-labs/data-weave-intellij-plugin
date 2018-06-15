package org.mule.tooling.bat.launcher;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;

public class BatTestConfigurationType implements ConfigurationType {

  private ConfigurationFactory weaveFactory;

  public BatTestConfigurationType() {
    weaveFactory = new BatTestConfigurationFactory(this);
  }

  @Override
  public String getDisplayName() {
    return "Bat Test";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Runs a bat test";
  }

  @Override
  public Icon getIcon() {
    return WeaveIcons.Bat;
  }

  @NotNull
  @Override
  public String getId() {
    return "org.mule.lang.battest.configuration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{weaveFactory};
  }

  @NotNull
  public static BatTestConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(BatTestConfigurationType.class);
  }

}
