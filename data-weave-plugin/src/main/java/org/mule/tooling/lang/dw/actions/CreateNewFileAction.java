package org.mule.tooling.lang.dw.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.templates.WeaveFilesTemplateManager;

public class CreateNewFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public CreateNewFileAction() {
        super("DataWeave Component", "Create new dataWeave.", WeaveIcons.DataWeaveModuleIcon);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("DataWeave Component")
                .addKind("Mapping", WeaveIcons.DataWeaveMappingIcon, WeaveFilesTemplateManager.DATA_WEAVE_FILE)
                .addKind("Module", WeaveIcons.DataWeaveModuleIcon, WeaveFilesTemplateManager.DATA_WEAVE_MODULE_FILE)
                .addKind("Unit test", WeaveIcons.DataWeaveTestIcon, WeaveFilesTemplateManager.DATA_WEAVE_UNIT_FILE)
                .addKind("Bat test", WeaveIcons.Bat, WeaveFilesTemplateManager.BAT_FILE);
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
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

