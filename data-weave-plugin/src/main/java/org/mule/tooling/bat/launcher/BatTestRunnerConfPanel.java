package org.mule.tooling.bat.launcher;

import com.intellij.application.options.ModulesComboBox;
import org.mule.tooling.lang.dw.ui.WeaveNameIdentifierSelector;

import javax.swing.*;

public class BatTestRunnerConfPanel {
  private JPanel mainPanel;
  private ModulesComboBox modules;
  private JTextField vmOptionsField;
  private WeaveNameIdentifierSelector nameIdentifierField;

  public BatTestRunnerConfPanel() {
    this.modules.addItemListener(e -> nameIdentifierField.setModule(modules.getSelectedModule()));
  }

  public WeaveNameIdentifierSelector getWeaveFile() {
    return nameIdentifierField;
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
