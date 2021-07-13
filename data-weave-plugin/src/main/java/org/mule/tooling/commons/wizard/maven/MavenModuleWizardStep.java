package org.mule.tooling.commons.wizard.maven;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectBundle;

import javax.swing.*;

public class MavenModuleWizardStep extends ModuleWizardStep {
    private static final Icon WIZARD_ICON = null;


    private MavenCoordsConfigurationStep ui = new MavenCoordsConfigurationStep();
    private MavenInfoModel model;


    public MavenModuleWizardStep(MavenInfoModel model) {
        this.model = model;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return ui.getArtifactId();
    }

    @Override
    public void updateStep() {
        super.updateStep();
        ui.getArtifactId().setText(model.getMavenId().getArtifactId());
        ui.getGroupId().setText(model.getMavenId().getGroupId());
        ui.getVersion().setText(model.getMavenId().getVersion());
    }

    @Override
    public JComponent getComponent() {
        return ui.getContainer();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (StringUtil.isEmptyOrSpaces(ui.getGroupId().getText())) {
            throw new ConfigurationException(MavenProjectBundle.message("dialog.message.wizard.please.specify.groupid"));
        }

        if (StringUtil.isEmptyOrSpaces(ui.getArtifactId().getText())) {
            throw new ConfigurationException(MavenProjectBundle.message("dialog.message.wizard.please.specify.artifactid"));
        }

        if (StringUtil.isEmptyOrSpaces(ui.getVersion().getText())) {
            throw new ConfigurationException(MavenProjectBundle.message("dialog.message.wizard.please.specify.version"));
        }

        return true;
    }


    @Override
    public void updateDataModel() {
        String version = ui.getVersion().getText();
        String artifactId = ui.getArtifactId().getText();
        String groupId = ui.getGroupId().getText();
        model.setMavenId(new MavenId(groupId, artifactId, version));
    }


    @Override
    public String getHelpId() {
        return "reference.dialogs.new.project.fromScratch.maven";
    }

    @Override
    public void disposeUIResources() {

    }
}