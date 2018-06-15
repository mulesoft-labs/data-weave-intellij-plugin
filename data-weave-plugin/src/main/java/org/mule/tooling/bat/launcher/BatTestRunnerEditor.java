package org.mule.tooling.bat.launcher;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;


public class BatTestRunnerEditor extends SettingsEditor<BatTestConfiguration> {

  private BatTestRunnerConfPanel configurationPanel;

  public BatTestRunnerEditor(BatTestConfiguration runnerConfiguration) {
    this.configurationPanel = new BatTestRunnerConfPanel(runnerConfiguration.getProject());
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded.
   * The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(BatTestConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
    this.configurationPanel.getWeaveFile().setText(runnerConfiguration.getNameIdentifier());
    this.configurationPanel.getVmOptionsField().setText(runnerConfiguration.getVmOptions());
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   * @throws ConfigurationException ex
   */
  @Override
  protected void applyEditorTo(BatTestConfiguration runnerConfiguration) throws ConfigurationException {
    final Module selectedModule = this.configurationPanel.getModuleCombo().getSelectedModule();
    if (selectedModule != null) {
      runnerConfiguration.setModule(selectedModule);
    }
    runnerConfiguration.setNameIdentifier(this.configurationPanel.getWeaveFile().getText());
    runnerConfiguration.setVmOptions(this.configurationPanel.getVmOptionsField().getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return this.configurationPanel.getMainPanel();
  }


}
