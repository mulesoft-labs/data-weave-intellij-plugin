package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;

public class WeaveTestConfigurationType implements ConfigurationType {

  private ConfigurationFactory weaveFactory;

  public WeaveTestConfigurationType() {
    weaveFactory = new WeaveTestConfigurationFactory(this);
  }

  @Override
  public String getDisplayName() {
    return "Weave Test";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Runs a weave test";
  }

  @Override
  public Icon getIcon() {
    return WeaveIcons.DataWeaveTestingFrameworkIcon;
  }

  @NotNull
  @Override
  public String getId() {
    return "org.mule.lang.weavetest.configuration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{weaveFactory};
  }

  @NotNull
  public static WeaveTestConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(WeaveTestConfigurationType.class);
  }

}
