package org.mule.tooling.lang.dw.settings;

import javax.swing.*;
import java.util.Objects;

public class DataWeaveSettingsUI {
    private JPanel root;
    private JTextField dotPathTextField;
    private JCheckBox showTypeInference;
    private JCheckBox showParametersName;
    private DataWeaveSettingsState settingsState;

    public DataWeaveSettingsUI(DataWeaveSettingsState settingsState) {
        this.settingsState = settingsState;
        dotPathTextField.setText(settingsState.getCmdPath());
    }

    public boolean isModified() {
        return !Objects.equals(settingsState.getCmdPath(), dotPathTextField.getText())
                || settingsState.getShowParametersName() != showParametersName.isSelected()
                || settingsState.getShowTypeInference() != showTypeInference.isSelected();
    }

    public JComponent getRoot() {
        return root;
    }

    public void apply() {
        this.settingsState.setCmdPath(dotPathTextField.getText());
        this.settingsState.setShowParametersName(showParametersName.isSelected());
        this.settingsState.setShowTypeInference(showTypeInference.isSelected());
    }

    public void reset() {
        this.dotPathTextField.setText(settingsState.getCmdPath());
        this.showTypeInference.setSelected(settingsState.getShowTypeInference());
        this.showParametersName.setSelected(settingsState.getShowParametersName());
    }
}
