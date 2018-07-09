package org.mule.tooling.runtime.munit.launcher;

import java.util.Collection;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;


public class MUnitTestRunnerEditor extends SettingsEditor<MUnitTestConfiguration> {

  private MUnitTestRunnerConfPanel configurationPanel;

  public MUnitTestRunnerEditor(MUnitTestConfiguration runnerConfiguration) {
    this.configurationPanel = new MUnitTestRunnerConfPanel();
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded. The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(@NotNull MUnitTestConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   */
  @Override
  protected void applyEditorTo(@NotNull MUnitTestConfiguration runnerConfiguration) {
    final Module selectedModule = this.configurationPanel.getModuleCombo().getSelectedModule();
    if (selectedModule != null) {
      runnerConfiguration.setModule(selectedModule);
    }
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return this.configurationPanel.getMainPanel();
  }


}
