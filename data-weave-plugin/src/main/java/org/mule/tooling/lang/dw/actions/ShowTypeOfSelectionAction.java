package org.mule.tooling.lang.dw.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LightweightHint;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import javax.swing.*;
import java.awt.*;

public class ShowTypeOfSelectionAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (editor != null && project != null) {
            boolean blockSelection = editor.getSelectionModel().getSelectionStart() != editor.getSelectionModel().getSelectionEnd();
            e.getPresentation().setEnabled(blockSelection);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (editor != null && project != null) {
            int selectionStart = editor.getSelectionModel().getSelectionStart();
            int selectionEnd = editor.getSelectionModel().getSelectionEnd();
            String weaveType = DWEditorToolingAPI.getInstance(project).typeOf(editor.getDocument(), selectionStart, selectionEnd);
            if (weaveType != null) {
                editor.getSelectionModel().getSelectionEnd();
                LightweightHint hint = new LightweightHint(new JLabel(weaveType));
                Point hintPosition = HintManagerImpl.getHintPosition(hint, editor, editor.getSelectionModel().getLeadSelectionPosition(), HintManager.ABOVE);
                HintManagerImpl.getInstanceImpl().showEditorHint(hint, editor, hintPosition, HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE, 0, false);
            }
        }

    }
}
