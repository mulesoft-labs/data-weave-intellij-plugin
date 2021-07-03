package org.mule.tooling.lang.dw.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import javax.swing.*;
import java.util.List;

public class WeaveScenarioComboBox extends JComboBox<Scenario> {

  private String nameIdentifier;
  private Module module;
  private String defaultScenario;

  public WeaveScenarioComboBox() {
    this.setRenderer(new ColoredListCellRenderer<Scenario>() {
      @Override
      protected void customizeCellRenderer(@NotNull JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
        if (value != null && value.getPresentableText() != null) {
          append(value.getPresentableText(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }
      }
    });
  }

  public void setNameIdentifier(String nameIdentifier) {
    this.nameIdentifier = nameIdentifier;
    loadModel();
  }

  public void setModule(Module module) {
    this.module = module;
    loadModel();
  }

  public void setScenario(String scenario) {
    defaultScenario = scenario;
    loadModel();
  }

  private void loadModel() {
    if (nameIdentifier != null && module != null) {
      final List<Scenario> scenariosFor = getScenarios();
      this.setModel(new ListComboBoxModel<Scenario>(scenariosFor));
      if (defaultScenario != null) {
        for (Scenario scenario: scenariosFor) {
          if (scenario.getName().equals(defaultScenario)) {
            this.setSelectedItem(scenario);
          }
        }
      }
    }
  }

  @Nullable
  public Scenario getSelectedScenario() {
    return (Scenario) getSelectedItem();
  }

  private List<Scenario> getScenarios() {
    final VirtualFile mappingFile = VirtualFileSystemUtils.resolve(module, NameIdentifier.apply(nameIdentifier, Option.empty()));
    final WeaveRuntimeService instance = WeaveRuntimeService.getInstance(module.getProject());
    return instance.getScenariosFor(mappingFile);
  }
}
