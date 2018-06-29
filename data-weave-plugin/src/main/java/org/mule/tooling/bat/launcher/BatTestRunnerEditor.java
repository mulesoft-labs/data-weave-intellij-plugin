package org.mule.tooling.bat.launcher;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;


public class BatTestRunnerEditor extends SettingsEditor<BatTestConfiguration> {

  private BatTestRunnerConfPanel configurationPanel;

  public BatTestRunnerEditor(BatTestConfiguration runnerConfiguration) {
    this.configurationPanel = new BatTestRunnerConfPanel();
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded.
   * The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(@NotNull BatTestConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
    this.configurationPanel.getWeaveFile().setNameIdentifier(runnerConfiguration.getNameIdentifier());
    this.configurationPanel.getWeaveFile().setModule(selectedModule);
    this.configurationPanel.getVmOptionsField().setText(runnerConfiguration.getVmOptions());
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   */
  @Override
  protected void applyEditorTo(@NotNull BatTestConfiguration runnerConfiguration) {
    final Module selectedModule = this.configurationPanel.getModuleCombo().getSelectedModule();
    if (selectedModule != null) {
      runnerConfiguration.setModule(selectedModule);
    }
    runnerConfiguration.setNameIdentifier(this.configurationPanel.getWeaveFile().getNameIdentifier());
    runnerConfiguration.setVmOptions(this.configurationPanel.getVmOptionsField().getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return this.configurationPanel.getMainPanel();
  }


}
