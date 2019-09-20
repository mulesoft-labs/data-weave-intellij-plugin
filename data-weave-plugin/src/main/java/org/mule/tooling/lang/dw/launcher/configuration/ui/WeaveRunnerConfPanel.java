package org.mule.tooling.lang.dw.launcher.configuration.ui;

import com.intellij.application.options.ModulesComboBox;
import org.mule.tooling.lang.dw.ui.WeaveNameIdentifierSelector;
import org.mule.tooling.lang.dw.ui.WeaveScenarioComboBox;

import javax.swing.*;

public class WeaveRunnerConfPanel {

  private JPanel mainPanel;
  private ModulesComboBox moduleCombo;
  private WeaveNameIdentifierSelector nameIdentifier;
  private WeaveScenarioComboBox scenario;
  private JTextField outputPath;

  public WeaveRunnerConfPanel() {
    moduleCombo.addItemListener(e -> {
      nameIdentifier.setModule(moduleCombo.getSelectedModule());
      scenario.setModule(moduleCombo.getSelectedModule());
    });
    this.nameIdentifier.addNameIdentifierSelectionListener(name -> scenario.setNameIdentifier(name));
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public ModulesComboBox getModuleCombo() {
    return moduleCombo;
  }

  public WeaveScenarioComboBox getScenario() {
    return scenario;
  }

  public WeaveNameIdentifierSelector getNameIdentifier() {
    return nameIdentifier;
  }

  public JTextField getOutputPath() {
    return outputPath;
  }
}


