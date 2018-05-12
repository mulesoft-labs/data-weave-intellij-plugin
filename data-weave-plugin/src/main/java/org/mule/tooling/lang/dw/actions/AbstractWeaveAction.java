package org.mule.tooling.lang.dw.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.mule.tooling.lang.dw.WeaveFileType;

public abstract class AbstractWeaveAction extends AnAction {

    public AbstractWeaveAction() {

    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        isDataWeaveContext(e);
    }

    public void isDataWeaveContext(AnActionEvent e) {
        Editor data = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getProject();
        boolean isEnabled = project != null && data != null && file != null && file.getFileType() == WeaveFileType.getInstance();
        e.getPresentation().setEnabled(isEnabled);
    }
}
