package org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui;

import com.intellij.application.options.ModulesComboBox;
import org.mule.tooling.lang.dw.ui.WeaveNameIdentifierSelector;

import javax.swing.*;

public class WeaveTestRunnerConfPanel {
  private JPanel mainPanel;
  private ModulesComboBox modules;
  private WeaveNameIdentifierSelector testField;
    private JTextField textField1;
  private JLabel testToRun;

  public WeaveTestRunnerConfPanel() {
    modules.addItemListener(e -> testField.setModule(modules.getSelectedModule()));
  }

  public WeaveNameIdentifierSelector getTestField() {
    return testField;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public ModulesComboBox getModuleCombo() {
    return modules;
  }
}
