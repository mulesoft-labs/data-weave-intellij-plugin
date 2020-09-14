package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui.WeaveIntegrationTestRunnerConfPanel;

import javax.swing.*;
import java.util.Collection;


public class WeaveIntegrationTestRunnerEditor extends SettingsEditor<WeaveIntegrationTestConfiguration> {

    private WeaveIntegrationTestRunnerConfPanel configurationPanel;

    public WeaveIntegrationTestRunnerEditor(WeaveIntegrationTestConfiguration runnerConfiguration) {
        this.configurationPanel = new WeaveIntegrationTestRunnerConfPanel(runnerConfiguration.getProject());
        super.resetFrom(runnerConfiguration);
    }


    /**
     * This is invoked when the form is first loaded.
     * The values may be stored in disk, if not, set some defaults
     */
    @Override
    protected void resetEditorFrom(@NotNull WeaveIntegrationTestConfiguration runnerConfiguration) {
        this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
        Module selectedModule = runnerConfiguration.getModule();
        if (selectedModule == null) {
            Collection<Module> modules = runnerConfiguration.getValidModules();
            if (modules.size() > 0) {
                selectedModule = modules.iterator().next();
            }
        }
        this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
        this.configurationPanel.getUpdateResult().setSelected(runnerConfiguration.isUpdateResult());
        this.configurationPanel.getTestToRun().setText(runnerConfiguration.getTestToRun());
        this.configurationPanel.getTestKind().setSelectedItem(runnerConfiguration.getTestKind());
        this.configurationPanel.getVmOptions().setText(runnerConfiguration.getVmOptions());
        this.configurationPanel.getWorkingDirectory().setText(runnerConfiguration.getWorkingDirectory());
    }

    /**
     * This is invoked when the user fills the form and pushes apply/ok
     *
     * @param runnerConfiguration runnerConfiguration
     */
    @Override
    protected void applyEditorTo(@NotNull WeaveIntegrationTestConfiguration runnerConfiguration) {
        final Module selectedModule = this.configurationPanel.getModuleCombo().getSelectedModule();
        if (selectedModule != null) {
            runnerConfiguration.setModule(selectedModule);
        }
        runnerConfiguration.setTestToRun(this.configurationPanel.getTestToRun().getText());
        runnerConfiguration.setUpdateResult(this.configurationPanel.getUpdateResult().isSelected());
        runnerConfiguration.setTestKind((IntegrationTestKind) this.configurationPanel.getTestKind().getSelectedItem());
        runnerConfiguration.setVmOptions(this.configurationPanel.getVmOptions().getText());
        runnerConfiguration.setWorkingDirectory(this.configurationPanel.getWorkingDirectory().getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return this.configurationPanel.getMainPanel();
    }


}
