package org.mule.tooling.restsdk.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenId;
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
        ui.getName().setText(model.getConnectorName());
        ui.getRestSdkVersion().setText(model.getRestSdkVersion());
        ui.getApiSpecTextField().setText(model.getApiSpec());
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return ui.getApiSpecTextField();
    }

    @Override
    public JComponent getComponent() {
        return ui.getContainer();
    }

    @Override
    public void updateDataModel() {
        model.setRestSdkVersion(ui.getRestSdkVersion().getText());
        model.setApiSpec(ui.getApiSpecTextField().getText());
        model.setConnectorName(ui.getName().getText());
        MavenId mavenId = mavenModel.getMavenId();
        mavenModel.setMavenId(new MavenId(mavenId.getGroupId(), ui.getName().getText(), mavenId.getVersion()));
    }

    @Override
    public boolean validate() throws ConfigurationException {
        VirtualFile apiSpecVirtualFile = ui.getApiSpecVirtualFile();
        if (ui.getApiSpecTextField().getText().isBlank() || apiSpecVirtualFile == null)
            throw new ConfigurationException("An API specification is required");
        if (!apiSpecVirtualFile.exists())
            throw new ConfigurationException("The specified API specification file does not exist");
        if (ui.getName().getText().isBlank())
            throw new ConfigurationException("A connector name is required");
        return super.validate();
    }
}
