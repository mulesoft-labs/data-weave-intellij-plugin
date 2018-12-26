package org.mule.tooling.lang.dw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DataWeaveSettings implements Configurable {

    private final DataWeaveSettingsUI dataWeaveSettingsUI;

    public DataWeaveSettings() {
        dataWeaveSettingsUI = new DataWeaveSettingsUI(DataWeaveSettingsState.getInstance());
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "DataWeave";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return dataWeaveSettingsUI.getRoot();
    }

    @Override
    public boolean isModified() {
        return dataWeaveSettingsUI.isModified();
    }

    @Override
    public void reset() {
        dataWeaveSettingsUI.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        dataWeaveSettingsUI.apply();
    }
}
