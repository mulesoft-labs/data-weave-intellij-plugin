package org.mule.tooling.lang.dw.launcher.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.ui.WeaveRunnerConfPanel;
import org.mule.tooling.lang.dw.service.Scenario;

import javax.swing.*;
import java.util.Collection;


public class WeaveRunnerEditor extends SettingsEditor<WeaveConfiguration> {

  private WeaveRunnerConfPanel configurationPanel;

  public WeaveRunnerEditor(WeaveConfiguration runnerConfiguration) {
    this.configurationPanel = new WeaveRunnerConfPanel();
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded.
   * The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(@NotNull WeaveConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
    this.configurationPanel.getNameIdentifier().setNameIdentifier(runnerConfiguration.getNameIdentifier());
    this.configurationPanel.getOutputPath().setText(runnerConfiguration.getOutputPath());
    this.configurationPanel.getScenario().setModule(selectedModule);
    this.configurationPanel.getScenario().setNameIdentifier(runnerConfiguration.getNameIdentifier());
    this.configurationPanel.getScenario().setScenario(runnerConfiguration.getScenario());
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   */
  @Override
  protected void applyEditorTo(@NotNull WeaveConfiguration runnerConfiguration) {
    Scenario scenario = this.configurationPanel.getScenario().getSelectedScenario();
    if (scenario != null) {
      runnerConfiguration.setScenario(scenario.getName());
    }
    runnerConfiguration.setNameIdentifier(this.configurationPanel.getNameIdentifier().getNameIdentifier());
    runnerConfiguration.setOutputPath(this.configurationPanel.getOutputPath().getText());
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
