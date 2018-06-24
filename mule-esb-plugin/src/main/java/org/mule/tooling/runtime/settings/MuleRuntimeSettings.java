package org.mule.tooling.runtime.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MuleRuntimeSettings implements Configurable {
  private MuleRuntimeSettingsUI muleRuntimeSettingsUI;

  public MuleRuntimeSettings() {
    muleRuntimeSettingsUI = new MuleRuntimeSettingsUI(MuleRuntimeSettingsState.getInstance());
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Anypoint Runtime";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return muleRuntimeSettingsUI.getRoot();
  }

  @Override
  public boolean isModified() {
    return muleRuntimeSettingsUI.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    muleRuntimeSettingsUI.apply();
  }

  @Override
  public void reset() {
    muleRuntimeSettingsUI.reset();
  }
}
