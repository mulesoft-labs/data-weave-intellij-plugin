package org.mule.tooling.lang.dw.surround;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DoBlockSurrounder extends AbstractTemplateSurrounder {

    @Override
    public String getTemplateDescription() {
        return "Wrap in do block";
    }

    @Override
    public Template createTemplate(@NotNull Project project, PsiElement expr) {
        final TemplateManager templateManager = TemplateManager.getInstance(project);
        return templateManager.createTemplate("do_surrounder", "dw_surrounder", "do {\n  " + expr.getText() + "\n}$END$");
    }
}
