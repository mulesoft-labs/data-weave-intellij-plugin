package org.mule.tooling.lang.dw.service;

import com.intellij.codeInsight.daemon.impl.quickfix.EmptyExpression;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.MacroParser;
import com.intellij.openapi.project.Project;
import org.mule.weave.v2.completion.IntellijTemplate;
import org.mule.weave.v2.completion.IntellijTemplateVariable;

public class IJAdapterHelper {
    public static Template toIJTemplate(Project project, org.mule.weave.v2.completion.Template template) {
        final IntellijTemplate intellijTemplate = template.toIntellijTemplate();
        final String templateText = intellijTemplate.text();
        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "dw_suggest", templateText);
        final IntellijTemplateVariable[] variables = intellijTemplate.variables();
        for (IntellijTemplateVariable variable : variables) {
            Expression defaultExpression = variable.defaultValue().isDefined() ? MacroParser.parse("\"" + variable.defaultValue().get() + "\"") : null;
            Expression expression = variable.defaultValue().isEmpty() ? MacroParser.parse("complete()") : new EmptyExpression();
            myTemplate.addVariable(variable.name(), expression, defaultExpression, true);
        }
        return myTemplate;
    }
}
