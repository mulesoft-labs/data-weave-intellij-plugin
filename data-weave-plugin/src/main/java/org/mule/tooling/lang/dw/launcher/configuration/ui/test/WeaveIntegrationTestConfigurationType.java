package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;

public class WeaveIntegrationTestConfigurationType implements ConfigurationType {

  private ConfigurationFactory weaveFactory;

  public WeaveIntegrationTestConfigurationType() {
    weaveFactory = new WeaveIntegrationTestConfigurationFactory(this);
  }

  @Override
  public String getDisplayName() {
    return "Weave Integration Test";
  }

  @Override
  public String getConfigurationTypeDescription() {
    return "Run weave integration tests.";
  }

  @Override
  public Icon getIcon() {
    return WeaveIcons.DataWeaveTestingFrameworkIcon;
  }

  @NotNull
  @Override
  public String getId() {
    return "org.mule.lang.weaveintegrationtest.configuration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{weaveFactory};
  }

  @NotNull
  public static WeaveIntegrationTestConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(WeaveIntegrationTestConfigurationType.class);
  }

}
