package org.mule.tooling.platform.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PlatformSettings implements Configurable {
    private PlatformSettingsUI platformSettingsUI;

    public PlatformSettings() {
        platformSettingsUI = new PlatformSettingsUI(PlatformSettingsState.getInstance());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Anypoint Platform";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return platformSettingsUI.getRoot();
    }

    @Override
    public boolean isModified() {
        return platformSettingsUI.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        platformSettingsUI.apply();
    }

    @Override
    public void reset() {
platformSettingsUI.reset();
    }
}
