package org.mule.tooling.bat.launcher;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class BatTestRunnerConfPanel {
  private final Project project;
  private JPanel mainPanel;
  private JTextField weaveFile;
  private ModulesComboBox modules;
  private JTextField vmOptionsField;

  public BatTestRunnerConfPanel(final Project project) {
    this.project = project;
  }

  public JTextField getWeaveFile() {
    return weaveFile;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public ModulesComboBox getModuleCombo() {
    return modules;
  }

  public JTextField getVmOptionsField() {
    return vmOptionsField;
  }
}
