package org.mule.tooling.lang.dw.settings;

import javax.swing.*;
import java.util.Objects;

public class DataWeaveSettingsUI {
    private JPanel root;
    private JTextField dotPathTextField;
    private DataWeaveSettingsState settingsState;

    public DataWeaveSettingsUI(DataWeaveSettingsState settingsState) {
        this.settingsState = settingsState;
        dotPathTextField.setText(settingsState.getCmdPath());
    }

    public boolean isModified() {
        return !Objects.equals(settingsState.getCmdPath(), dotPathTextField.getText());
    }

    public JComponent getRoot() {
        return root;
    }

    public void apply() {
        this.settingsState.setCmdPath(dotPathTextField.getText());
    }

    public void reset() {
        this.dotPathTextField.setText(settingsState.getCmdPath());
    }
}
