package org.mule.tooling.restsdk.wizzard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class RestSdkConfigurationStep extends ModuleWizardStep {

    private RestSdkConfigurationModel model;
    private RestSdkModuleConfigurationStep ui = new RestSdkModuleConfigurationStep();

    public RestSdkConfigurationStep(RestSdkConfigurationModel model) {
        this.model = model;
        updateStep();
    }

    @Override
    public void updateStep() {
        super.updateStep();
        ui.getRestSdkVersion().setText(model.getRestSdkVersion());
        ui.getApiKind().setSelectedItem(model.getApiKind());
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return ui.getName();
    }

    @Override
    public JComponent getComponent() {
        return ui.getContainer();
    }

    @Override
    public void updateDataModel() {
        model.setRestSdkVersion(ui.getRestSdkVersion().getText());
        model.setApiKind((ApiKind) ui.getApiKind().getSelectedItem());
        model.setConnectorName(ui.getName().getText());
    }
}
