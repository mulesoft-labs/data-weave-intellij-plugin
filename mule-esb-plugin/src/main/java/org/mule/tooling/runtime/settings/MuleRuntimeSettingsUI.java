package org.mule.tooling.runtime.settings;

import javax.swing.*;

public class MuleRuntimeSettingsUI {
  private JTextField defaultMuleVersion;
  private JPanel root;

  private MuleRuntimeSettingsState state;

  public MuleRuntimeSettingsUI(MuleRuntimeSettingsState state) {
    this.state = state;

    loadFromState();
  }

  public JPanel getRoot() {
    return root;
  }

  private void loadFromState() {
    this.defaultMuleVersion.setText(this.state.getDefaultRuntimeVersion());
  }

  public boolean isModified() {
    return !getDefaultMuleVersionValue().equals(this.state.getDefaultRuntimeVersion());
  }

  public void apply() {
    this.state.setDefaultRuntimeVersion(getDefaultMuleVersionValue());
  }

  private String getDefaultMuleVersionValue() {
    return defaultMuleVersion.getText();
  }

  public void reset() {
    loadFromState();
  }
}
