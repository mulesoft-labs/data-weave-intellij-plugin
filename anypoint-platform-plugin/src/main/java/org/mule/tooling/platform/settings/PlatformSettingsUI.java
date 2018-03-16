package org.mule.tooling.platform.settings;

import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.ColoredListCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.platform.PlatformRegion;

import javax.swing.*;

import static com.intellij.ui.SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES;

public class PlatformSettingsUI {
    private JTextField url;
    private JCheckBox onPremiseCheckBox;
    private JPanel root;
    private JComboBox regions;
    private PlatformSettingsState state;

    public PlatformSettingsUI(PlatformSettingsState state) {
        this.state = state;
        getOnPremiseCheckBox().addChangeListener(e -> url.setEnabled(getOnPremiseCheckBox().isSelected()));

        this.regions.setModel(new CollectionComboBoxModel(PlatformRegion.getRegions()));
        this.regions.setRenderer(new ColoredListCellRenderer<PlatformRegion>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList jList, PlatformRegion o, int i, boolean b, boolean b1) {
                this.append(o.getName());
                this.append(" (" + o.getPrefix() + ")", GRAY_ITALIC_ATTRIBUTES);
            }
        });
        loadFromState();
    }

    private void loadFromState() {
        getUrl().setText(state.getCustomUrl());
        getUrl().setEnabled(state.isOnPremise());
        getOnPremiseCheckBox().setSelected(state.isOnPremise());
        this.regions.setSelectedItem(state.getRegion());
    }

    public JTextField getUrl() {
        return url;
    }

    public JCheckBox getOnPremiseCheckBox() {
        return onPremiseCheckBox;
    }

    public JPanel getRoot() {
        return root;
    }

    public boolean isModified() {
        return getOnPremiseCheckBox().isSelected() != state.isOnPremise() ||
                !getUrl().getText().equals(state.getCustomUrl()) ||
                !regions.getSelectedItem().equals(state.getRegion())
                ;
    }

    public void apply() {
        state.setOnPremise(getOnPremiseCheckBox().isSelected());
        state.setCustomUrl(getUrl().getText());
        state.setRegion((PlatformRegion) regions.getSelectedItem());
    }

    public void reset() {
        loadFromState();
    }
}
