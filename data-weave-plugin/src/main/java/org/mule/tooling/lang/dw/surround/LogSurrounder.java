package org.mule.tooling.lang.dw.surround;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LogSurrounder extends AbstractTemplateSurrounder {

    @Override
    public String getTemplateDescription() {
        return "Log the selected expression";
    }

    @Override
    public Template createTemplate(@NotNull Project project, PsiElement expr) {
        final TemplateManager templateManager = TemplateManager.getInstance(project);
        return templateManager.createTemplate("log_surrounder", "dw_surrounder", "log(" + expr.getText() + ")$END$");
    }
}
