package org.mule.tooling.runtime.munit.launcher;

import javax.swing.*;

import com.intellij.application.options.ModulesComboBox;

public class MUnitTestRunnerConfPanel {

  private JPanel mainPanel;
  private ModulesComboBox modules;

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public ModulesComboBox getModuleCombo() {
    return modules;
  }
}
