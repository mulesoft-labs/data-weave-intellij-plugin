package org.mule.tooling.lang.dw.ui;

import com.intellij.ide.util.gotoByName.ChooseByNamePanel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.navigation.GotoMappingContributor;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;

import javax.swing.*;
import java.awt.*;

public class WeaveNameIdentifierChooserDialog extends DialogWrapper {

  private ChooseByNamePanel chooseByNamePanel;
  private BorderLayoutPanel dummyPanel;
  private WeaveDocument choosenElement;


  protected WeaveNameIdentifierChooserDialog(Module module, String initialValue) {
    super(module.getProject());

    this.dummyPanel = new BorderLayoutPanel();
    chooseByNamePanel = new WeaveChooseByNamePanel(module, initialValue);
    chooseByNamePanel.invoke(new MyCallback(), ModalityState.defaultModalityState(), false);
    setButtonsMargin(null);
    setOKButtonText("Ok");
    setTitle("Select Weave File");
    init();
    IdeFocusTraversalPolicy.getPreferredFocusedComponent(chooseByNamePanel.getPreferredFocusedComponent()).requestFocus();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    BorderLayoutPanel root = new BorderLayoutPanel();
    root.addToCenter(chooseByNamePanel.getPanel());
    return root;
  }

  @Override
  protected void doOKAction() {
    choosenElement = (WeaveDocument) chooseByNamePanel.getChosenElement();
    super.doOKAction();
  }

  @Override
  public void doCancelAction() {
    super.doCancelAction();
    choosenElement = null;
  }

  @Nullable
  public String getSelectedNameIdentifier() {
    if (choosenElement != null) {
      return choosenElement.getQualifiedName();
    } else {
      return null;
    }
  }

  private class MyCallback extends ChooseByNamePopupComponent.Callback {
    @Override
    public void elementChosen(Object element) {
      choosenElement = (WeaveDocument) element;
      close(OK_EXIT_CODE);
    }
  }

  private class WeaveChooseByNamePanel extends ChooseByNamePanel {
    public WeaveChooseByNamePanel(Module module, String initialValue) {
      super(module.getProject(), new GotoWeaveFileModel(module), initialValue, false, null);
    }

    @Override
    protected void close(final boolean isOk) {
      super.close(isOk);
      if (isOk) {
        doOKAction();
      } else {
        doCancelAction();
      }
    }

    @Override
    protected void initUI(final ChooseByNamePopupComponent.Callback callback,
                          final ModalityState modalityState,
                          boolean allowMultipleSelection) {
      super.initUI(callback, modalityState, allowMultipleSelection);
      dummyPanel.add(chooseByNamePanel.getPanel(), BorderLayout.CENTER);
      IdeFocusTraversalPolicy.getPreferredFocusedComponent(chooseByNamePanel.getPreferredFocusedComponent()).requestFocus();
    }

    @Override
    protected void showTextFieldPanel() {
    }
  }

  private static class GotoWeaveFileModel extends GotoClassModel2 {
    public GotoWeaveFileModel(Module module) {
      super(module.getProject());
    }

    @Override
    protected ChooseByNameContributor[] getContributors() {
      return new ChooseByNameContributor[]{new GotoMappingContributor()};
    }

    @Nullable
    @Override
    public String getPromptText() {
      return "Enter weave file name: ";
    }
  }
}
