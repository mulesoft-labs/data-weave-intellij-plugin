package org.mule.tooling.lang.dw.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.components.JBTextField;

import java.util.ArrayList;
import java.util.List;

public class WeaveNameIdentifierSelector extends ComponentWithBrowseButton<JBTextField> {

  private List<NameIdentifierSelectionListener> listeners;

  public WeaveNameIdentifierSelector() {
    super(new JBTextField(), null);
    this.setButtonEnabled(false);
    this.listeners = new ArrayList<>();

  }

  public void setModule(Module module) {
    this.setButtonEnabled(module != null);
    this.addActionListener(e -> {
      assert module != null;
      WeaveNameIdentifierChooserDialog chooserDialog = new WeaveNameIdentifierChooserDialog(module, getChildComponent().getText());
      chooserDialog.show();
      String selectedNameIdentifier = chooserDialog.getSelectedNameIdentifier();
      if (selectedNameIdentifier != null) {
        getChildComponent().setText(selectedNameIdentifier);
        for (NameIdentifierSelectionListener listener: listeners) {
          listener.onNameIdentifierSelected(selectedNameIdentifier);
        }
      }
    });
  }

  public void setNameIdentifier(String nameIdentifier) {
    getChildComponent().setText(nameIdentifier);
  }

  public String getNameIdentifier() {
    return getChildComponent().getText();
  }

  public void addNameIdentifierSelectionListener(NameIdentifierSelectionListener listener) {
    this.listeners.add(listener);
  }

  public interface NameIdentifierSelectionListener {
    void onNameIdentifierSelected(String name);
  }
}
