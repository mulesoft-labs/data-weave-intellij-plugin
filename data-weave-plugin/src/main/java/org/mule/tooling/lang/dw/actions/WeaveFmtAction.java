package org.mule.tooling.lang.dw.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

public class WeaveFmtAction extends AbstractWeaveAction implements DumbAware {

    public WeaveFmtAction() {
        setInjectedContext(true);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor data = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();
        CommandProcessor.getInstance().executeCommand(project, () -> DWEditorToolingAPI.getInstance(project).reformat(data.getDocument()), "Reformat DataWeave", "dataweave");
    }
}
