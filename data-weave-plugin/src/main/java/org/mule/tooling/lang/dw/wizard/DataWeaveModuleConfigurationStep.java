package org.mule.tooling.lang.dw.wizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataWeaveModuleConfigurationStep {
    private JTextField dwVersionField;
    private JTextField dwtfVersion;
    private JTextField dwPluginVersion;
    private JCheckBox deployToExchange;
    private JTextField orgIdField;
    private JPanel container;

    public DataWeaveModuleConfigurationStep() {
        deployToExchange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orgIdField.setEnabled(deployToExchange.isSelected());
            }
        });
    }

    public JTextField getDwVersionField() {
        return dwVersionField;
    }

    public JTextField getDwtfVersion() {
        return dwtfVersion;
    }

    public JTextField getDwPluginVersion() {
        return dwPluginVersion;
    }

    public JCheckBox getDeployToExchange() {
        return deployToExchange;
    }

    public JTextField getOrgIdField() {
        return orgIdField;
    }

    public JPanel getContainer() {
        return container;
    }
}
