package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui.WeaveTestRunnerConfPanel;


import javax.swing.*;
import java.util.Collection;


public class WeaveTestRunnerEditor extends SettingsEditor<WeaveTestConfiguration> {

  private WeaveTestRunnerConfPanel configurationPanel;

  public WeaveTestRunnerEditor(WeaveTestConfiguration runnerConfiguration) {
    this.configurationPanel = new WeaveTestRunnerConfPanel();
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded.
   * The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(@NotNull WeaveTestConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
    this.configurationPanel.getTestField().setModule(selectedModule);
    this.configurationPanel.getTestField().setNameIdentifier(runnerConfiguration.getTests().get(0));
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   */
  @Override
  protected void applyEditorTo(@NotNull WeaveTestConfiguration runnerConfiguration) {
    runnerConfiguration.setWeaveFile(this.configurationPanel.getTestField().getNameIdentifier());
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
