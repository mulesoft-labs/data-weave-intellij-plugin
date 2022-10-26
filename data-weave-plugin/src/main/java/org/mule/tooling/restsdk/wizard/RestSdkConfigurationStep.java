package org.mule.tooling.restsdk.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import org.mule.tooling.commons.wizard.maven.MavenInfoModel;

import javax.swing.*;

public class RestSdkConfigurationStep extends ModuleWizardStep {

    private final RestSdkConfigurationModel model;
    private final MavenInfoModel mavenModel;
    private final RestSdkModuleConfigurationStep ui = new RestSdkModuleConfigurationStep();

    public RestSdkConfigurationStep(RestSdkConfigurationModel model, MavenInfoModel mavenModel) {
        this.model = model;
        this.mavenModel = mavenModel;
        updateStep();
    }

    @Override
    public void updateStep() {
        super.updateStep();
        ui.getName().setText(mavenModel.getMavenId().getArtifactId());
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
