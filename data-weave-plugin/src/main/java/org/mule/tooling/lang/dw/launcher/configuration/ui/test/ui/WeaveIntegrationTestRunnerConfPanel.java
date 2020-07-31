package org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui;

import com.intellij.application.options.ModulesComboBox;

import javax.swing.*;

public class WeaveIntegrationTestRunnerConfPanel {
    private JPanel mainPanel;
    private ModulesComboBox modules;
    private JTextField testToRun;
    private JCheckBox updateResult;


    public WeaveIntegrationTestRunnerConfPanel() {

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ModulesComboBox getModuleCombo() {
        return modules;
    }

    public JTextField getTestToRun() {
        return testToRun;
    }

    public JCheckBox getUpdateResult() {
        return updateResult;
    }
}
