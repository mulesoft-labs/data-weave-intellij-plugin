package org.mule.tooling.lang.dw.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

public class WeaveFmtAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Editor data = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getProject();
        e.getPresentation().setEnabled(project != null && data != null && file != null && file.isInLocalFileSystem() && file.getFileType() == WeaveFileType.getInstance());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor data = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();
        CommandProcessor.getInstance().executeCommand(project, () -> DWEditorToolingAPI.getInstance(project).reformat(data.getDocument()), "Reformat DataWeave", "dataweave");
    }
}
