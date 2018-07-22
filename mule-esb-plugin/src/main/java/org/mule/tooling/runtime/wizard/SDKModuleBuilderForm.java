package org.mule.tooling.runtime.wizard;

import static org.mule.tooling.runtime.wizard.sdk.SdkWizardConstants.MIN_MULE_VERSION_TOOLTIP;
import static org.mule.tooling.runtime.wizard.sdk.SdkWizardConstants.MTF_TEST_TOOLTIP;

import org.mule.tooling.runtime.wizard.sdk.SdkProject;
import org.mule.tooling.runtime.wizard.sdk.SdkType;

import java.awt.event.ItemEvent;

import javax.swing.*;

public class SDKModuleBuilderForm {
    private JPanel sdkWizardPanel;
    private JTextField extensionNameTextField;
    private JRadioButton createMTFTestsRadioButton;
    private JRadioButton createJavaTestsRadioButton;
    private JRadioButton createScopesRadioButton;
    private JRadioButton createSourceRadioButton;
    private JComboBox<SdkType> sdkTypeComboBox;
    private JComboBox<Version> versionComboBox;
    private JLabel minMuleVersion;
    private boolean previousState = false;

    SDKModuleBuilderForm() {
        initSdkVersionComboBox();
        initMuleVersionLabel();
        initSdkTypeComboBox();

        createMTFTestsRadioButton.setToolTipText(MTF_TEST_TOOLTIP);
    }

    private void initSdkTypeComboBox() {
        sdkTypeComboBox.addItem(SdkType.JAVA);
        sdkTypeComboBox.addItem(SdkType.XML);
        sdkTypeComboBox.setSelectedIndex(0);
        sdkTypeComboBox.addItemListener((ItemEvent e) -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SdkType type = (SdkType) e.getItem();
                switch (type) {
                    case XML: {
                        previousState = createSourceRadioButton.isSelected();
                        createSourceRadioButton.setSelected(false);
                        createSourceRadioButton.setEnabled(false);
                        break;
                    }
                    case JAVA: {
                        createSourceRadioButton.setSelected(previousState);
                        createSourceRadioButton.setEnabled(true);
                        break;
                    }
                }
            }
        });
    }

    private void initMuleVersionLabel() {
        minMuleVersion.setToolTipText(MIN_MULE_VERSION_TOOLTIP);
        Version selectedItem = (Version) versionComboBox.getSelectedItem();
        minMuleVersion.setText("(Compatible with " + selectedItem.getMinMuleVersion() + "+)");
    }

    private void initSdkVersionComboBox() {
        versionComboBox.addItem(new Version("1.1.0", true, "Mule 4.1.0"));
        versionComboBox.addItem(new Version("1.1.1", false, "Mule 4.1.1"));
        versionComboBox.addItemListener(e -> {
            Version item = (Version) e.getItem();
            minMuleVersion.setText("(Compatible with " + item.getMinMuleVersion() + "+)");
        });
        versionComboBox.setSelectedIndex(0);
    }

    public SdkProject getProject() {
        return new SdkProject(extensionNameTextField.getText(),
                ((Version) versionComboBox.getSelectedItem()).version,
                (SdkType) sdkTypeComboBox.getSelectedItem(),
                createJavaTestsRadioButton.isSelected(),
                createMTFTestsRadioButton.isSelected(),
                createSourceRadioButton.isSelected());
    }

    public static class Version {

        public Version(String version, Boolean recommended, String minMuleVersion) {
            this.version = version;
            this.recommended = recommended;
            this.minMuleVersion = minMuleVersion;
        }

        String version;
        Boolean recommended;
        private String minMuleVersion;

        public String getVersion() {
            return version;
        }

        public String getMinMuleVersion() {
            return minMuleVersion;
        }

        @Override
        public String toString() {
            return version + (recommended ? " (Recommended)" : "");
        }
    }

    JPanel getSdkWizardPanel() {
        return sdkWizardPanel;
    }
}
