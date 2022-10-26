package org.mule.tooling.restsdk.wizard;

import com.intellij.ui.EnumComboBoxModel;

import javax.swing.*;

public class RestSdkModuleConfigurationStep {
    private JTextField restSdkVersion;
    private JPanel container;
    private JComboBox<ApiKind> apiKind;
    private JTextField name;

    public RestSdkModuleConfigurationStep() {
        apiKind.setModel(new EnumComboBoxModel<>(ApiKind.class));
    }

    public JTextField getRestSdkVersion() {
        return restSdkVersion;
    }

    public JTextField getName() {
        return name;
    }

    public JComboBox<ApiKind> getApiKind() {
        return apiKind;
    }

    public JPanel getContainer() {
        return container;
    }
}
