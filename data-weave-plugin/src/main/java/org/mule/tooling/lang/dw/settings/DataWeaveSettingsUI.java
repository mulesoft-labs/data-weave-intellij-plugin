package org.mule.tooling.lang.dw.settings;

import javax.swing.*;
import java.util.Objects;

public class DataWeaveSettingsUI {
    private JPanel root;
    private JTextField dotPathTextField;
    private JCheckBox showTypeInference;
    private JCheckBox showParametersName;
    private JSpinner maxLineNumbers;
    private JSpinner maxTime;
    private DataWeaveSettingsState settingsState;

    public DataWeaveSettingsUI(DataWeaveSettingsState settingsState) {
        this.settingsState = settingsState;
        dotPathTextField.setText(settingsState.getCmdPath());
    }

    public boolean isModified() {
        return !Objects.equals(settingsState.getCmdPath(), dotPathTextField.getText())
                || settingsState.getShowParametersName() != showParametersName.isSelected()
                || settingsState.getShowTypeInference() != showTypeInference.isSelected()
                || ((Integer) settingsState.getMaxAmountOfCharsForSemanticAnalysis()) != maxLineNumbers.getValue()
                || ((Integer) settingsState.getMaxTimePreview()) != maxTime.getValue();
    }

    public JComponent getRoot() {
        return root;
    }

    public void apply() {
        this.settingsState.setCmdPath(dotPathTextField.getText());
        this.settingsState.setShowParametersName(showParametersName.isSelected());
        this.settingsState.setShowTypeInference(showTypeInference.isSelected());
        this.settingsState.setMaxAmountOfCharsForSemanticAnalysis((Integer) maxLineNumbers.getValue());
        this.settingsState.setMaxTimePreview((Integer) maxTime.getValue());
    }

    public void reset() {
        this.dotPathTextField.setText(settingsState.getCmdPath());
        this.showTypeInference.setSelected(settingsState.getShowTypeInference());
        this.showParametersName.setSelected(settingsState.getShowParametersName());
        this.maxLineNumbers.setValue(settingsState.getMaxAmountOfCharsForSemanticAnalysis());
        this.maxTime.setValue(settingsState.getMaxTimePreview());
    }
}
