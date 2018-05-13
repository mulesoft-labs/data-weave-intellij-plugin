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
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import javax.swing.*;
import java.awt.*;

public class ShowTypeOfSelectionAction extends AbstractWeaveAction {


    public ShowTypeOfSelectionAction() {
        setInjectedContext(true);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (editor != null && project != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            int selectionStart = selectionModel.getSelectionStart();
            int selectionEnd = selectionModel.getSelectionEnd();
            String weaveType = DWEditorToolingAPI.getInstance(project).typeOf(editor.getDocument(), selectionStart, selectionEnd);
            if (weaveType != null) {
                selectionModel.getSelectionEnd();
                LightweightHint hint = new LightweightHint(new JLabel(weaveType));
                VisualPosition selectionStartPosition = selectionModel.getSelectionStartPosition();
                VisualPosition selectionEndPosition = selectionModel.getSelectionEndPosition();
                if (selectionStartPosition != null && selectionEndPosition != null) {
                    VisualPosition visualPosition = selectionStartPosition.line == selectionEndPosition.line ? new VisualPosition(selectionStartPosition.line, (selectionStartPosition.column + selectionEndPosition.column) / 2, selectionStartPosition.leansRight) : selectionStartPosition;
                    Point hintPosition = HintManagerImpl.getHintPosition(hint, editor, visualPosition, HintManager.ABOVE);
                    HintManagerImpl.getInstanceImpl().showEditorHint(hint, editor, hintPosition, HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE, 0, false);
                }
            }
        }

    }
}
