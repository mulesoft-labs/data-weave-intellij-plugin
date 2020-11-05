package org.mule.tooling.lang.dw.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class DataWeaveConfigurationStep extends ModuleWizardStep {


    DataWeaveConfigurationModel model;
    private DataWeaveModuleConfigurationStep ui = new DataWeaveModuleConfigurationStep();

    public DataWeaveConfigurationStep(DataWeaveConfigurationModel model) {
        this.model = model;
        updateStep();
    }

    @Override
    public void updateStep() {
        super.updateStep();
        ui.getDwtfVersion().setText(model.getWtfVersion());
        ui.getDwPluginVersion().setText(model.getWeaveMavenVersion());
        ui.getDwVersionField().setText(model.getWeaveMavenVersion());

    }

    @Override
    public JComponent getComponent() {
        return ui.getContainer();
    }

    @Override
    public void updateDataModel() {
        model.setWeaveMavenVersion(ui.getDwPluginVersion().getText());
        model.setWtfVersion(ui.getDwtfVersion().getText());
        model.setWeaveVersion(ui.getDwVersionField().getText());
        if (ui.getDeployToExchange().isSelected()) {
            model.setOrgId(ui.getDwVersionField().getText());
        }
    }


}
