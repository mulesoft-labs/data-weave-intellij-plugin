package org.mule.tooling.als.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ALSSettings implements Configurable {

  private ALSSettingUI settingsUI;

  public ALSSettings() {
    this.settingsUI = new ALSSettingUI();
  }

  @Override
  public @NlsContexts.ConfigurableName String getDisplayName() {
    return "ALS";
  }

  @Override
  public @Nullable JComponent createComponent() {
    return settingsUI.getTopField();
  }

  @Override
  public boolean isModified() {
    return true;
  }

  @Override
  public void apply() throws ConfigurationException {
    DialectsRegistry.getInstance().setDialectsRegistry(settingsUI.dialects());
  }
}
