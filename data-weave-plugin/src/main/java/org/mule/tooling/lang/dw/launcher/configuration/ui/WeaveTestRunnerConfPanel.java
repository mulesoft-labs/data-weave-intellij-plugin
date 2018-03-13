package org.mule.tooling.lang.dw.launcher.configuration.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class WeaveTestRunnerConfPanel {
    private final Project project;
    private JPanel mainPanel;
    private JTextField weaveFile;
    private ModulesComboBox modules;

    public WeaveTestRunnerConfPanel(final Project project) {
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
}
