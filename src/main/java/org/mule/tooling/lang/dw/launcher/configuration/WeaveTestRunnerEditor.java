package org.mule.tooling.lang.dw.launcher.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.ui.WeaveRunnerConfPanel;
import org.mule.tooling.lang.dw.launcher.configuration.ui.WeaveTestRunnerConfPanel;

import javax.swing.*;
import java.util.Collection;


public class WeaveTestRunnerEditor extends SettingsEditor<WeaveTestConfiguration> {

  private WeaveTestRunnerConfPanel configurationPanel;

  public WeaveTestRunnerEditor(WeaveTestConfiguration runnerConfiguration) {
    this.configurationPanel = new WeaveTestRunnerConfPanel(runnerConfiguration.getProject());
    super.resetFrom(runnerConfiguration);
  }


  /**
   * This is invoked when the form is first loaded.
   * The values may be stored in disk, if not, set some defaults
   */
  @Override
  protected void resetEditorFrom(WeaveTestConfiguration runnerConfiguration) {
    this.configurationPanel.getModuleCombo().setModules(runnerConfiguration.getValidModules());
    Module selectedModule = runnerConfiguration.getModule();
    if (selectedModule == null) {
      Collection<Module> modules = runnerConfiguration.getValidModules();
      if (modules.size() > 0) {
        selectedModule = modules.iterator().next();
      }
    }
    this.configurationPanel.getModuleCombo().setSelectedModule(selectedModule);
    this.configurationPanel.getWeaveFile().setText(runnerConfiguration.getWeaveFile());
  }

  /**
   * This is invoked when the user fills the form and pushes apply/ok
   *
   * @param runnerConfiguration runnerConfiguration
   * @throws ConfigurationException ex
   */
  @Override
  protected void applyEditorTo(WeaveTestConfiguration runnerConfiguration) throws ConfigurationException {
    runnerConfiguration.setWeaveFile(this.configurationPanel.getWeaveFile().getText());
    final Module selectedModule = this.configurationPanel.getModuleCombo().getSelectedModule();
    if (selectedModule != null) {
      runnerConfiguration.setModule(selectedModule);
    }
    runnerConfiguration.setWeaveFile(this.configurationPanel.getWeaveFile().getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return this.configurationPanel.getMainPanel();
  }


}
