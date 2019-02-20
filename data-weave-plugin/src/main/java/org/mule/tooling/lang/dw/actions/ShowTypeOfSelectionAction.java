package org.mule.tooling.lang.dw.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LightweightHint;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

import javax.swing.*;
import java.awt.*;

public class ShowTypeOfSelectionAction extends AbstractWeaveAction {

  public ShowTypeOfSelectionAction() {
    setInjectedContext(true);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    final Project project = e.getData(CommonDataKeys.PROJECT);
    if (editor != null && project != null) {
      final SelectionModel selectionModel = editor.getSelectionModel();
      final int selectionStart = selectionModel.getSelectionStart();
      final int selectionEnd = selectionModel.getSelectionEnd();
      String weaveType = WeaveEditorToolingAPI.getInstance(project).typeOf(editor.getDocument(), selectionStart, selectionEnd);
      if (weaveType == null) {
        weaveType = "Unable to infer type of expression.";
      }
      final LightweightHint hint = new LightweightHint(new JLabel(weaveType));
      final VisualPosition selectionStartPosition = selectionModel.getSelectionStartPosition();
      final VisualPosition selectionEndPosition = selectionModel.getSelectionEndPosition();
      if (selectionStartPosition != null && selectionEndPosition != null) {
        final VisualPosition visualPosition;
        if (selectionStartPosition.line == selectionEndPosition.line) {
          visualPosition = new VisualPosition(selectionStartPosition.line, (selectionStartPosition.column + selectionEndPosition.column) / 2, selectionStartPosition.leansRight);
        } else {
          visualPosition = selectionStartPosition;
        }

        final Point hintPosition = HintManagerImpl.getHintPosition(hint, editor, visualPosition, HintManager.ABOVE);
        HintManagerImpl.getInstanceImpl().showEditorHint(hint, editor, hintPosition, HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE, 0, false);
      }
    }
  }
}
