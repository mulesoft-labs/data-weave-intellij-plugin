package org.mule.tooling.runtime.launcher.configuration.ui;

import javax.swing.*;

public class MuleRunnerConfPanel {
  private JTextField vmArgsField;
  private JPanel mainPanel;
  //private ModulesComboBox moduleCombo;
//  private MuleSdkComboSelection muleSdkSelector;
  private MuleModulesCheckBoxList modulesList;

  private JRadioButton alwaysRadioButton;
  private JRadioButton neverRadioButton;
  private JRadioButton promptRadioButton;


  public MuleRunnerConfPanel() {

  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public JTextField getVmArgsField() {
    return vmArgsField;
  }

  public JComboBox getMuleHome() {
//    return muleSdkSelector.getMuleSdk();
    return null;
  }

  public MuleModulesCheckBoxList getModulesList() {
    return modulesList;
  }

  public JRadioButton getAlwaysRadioButton() {
    return alwaysRadioButton;
  }

  public JRadioButton getNeverRadioButton() {
    return neverRadioButton;
  }

  public JRadioButton getPromptRadioButton() {
    return promptRadioButton;
  }
}
