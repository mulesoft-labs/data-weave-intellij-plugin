package org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.ui.EnumComboBoxModel;
import org.mule.tooling.lang.dw.launcher.configuration.ui.test.IntegrationTestKind;

import javax.swing.*;

public class WeaveIntegrationTestRunnerConfPanel {
    private JPanel mainPanel;
    private ModulesComboBox moduleCombo;
    private JTextField testToRun;
    private JCheckBox updateResult;
    private JComboBox<IntegrationTestKind> testKind;
    private JTextField vmOptions;


    public WeaveIntegrationTestRunnerConfPanel() {
        testKind.setModel(new EnumComboBoxModel<IntegrationTestKind>(IntegrationTestKind.class));
    }

    public JTextField getVmOptions() {
        return vmOptions;
    }

    public JComboBox<IntegrationTestKind> getTestKind() {
        return testKind;
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ModulesComboBox getModuleCombo() {
        return moduleCombo;
    }

    public JTextField getTestToRun() {
        return testToRun;
    }

    public JCheckBox getUpdateResult() {
        return updateResult;
    }
}
