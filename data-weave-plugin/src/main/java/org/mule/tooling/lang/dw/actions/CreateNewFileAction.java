package org.mule.tooling.lang.dw.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.templates.WeaveFilesTemplateManager;

public class CreateNewFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public CreateNewFileAction() {
        super("DataWeave Component", "Create New DataWeave.", WeaveIcons.WeaveFileType);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("DataWeave Component")
                .addKind("Mapping", WeaveIcons.WeaveFileType, WeaveFilesTemplateManager.DATA_WEAVE_FILE)
                .addKind("Module", WeaveIcons.WeaveFileType, WeaveFilesTemplateManager.DATA_WEAVE_MODULE_FILE)
                .addKind("Unit Test", WeaveIcons.WeaveTestFileType, WeaveFilesTemplateManager.DATA_WEAVE_UNIT_FILE)
                .addKind("Bat Test", WeaveIcons.Bat, WeaveFilesTemplateManager.BAT_FILE);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Create " + newName;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CreateNewFileAction;
    }
}

