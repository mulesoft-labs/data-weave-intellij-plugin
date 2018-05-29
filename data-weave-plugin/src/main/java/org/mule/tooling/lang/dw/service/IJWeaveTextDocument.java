package org.mule.tooling.lang.dw.service;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.mule.weave.v2.completion.Template;
import org.mule.weave.v2.editor.WeaveTextDocument;

public class IJWeaveTextDocument implements WeaveTextDocument {
    private final Editor editor;
    private Project project;

    public IJWeaveTextDocument(Editor editor, Project project) {
        this.editor = editor;
        this.project = project;
    }

    @Override
    public void runTemplate(Template template, int location) {
        WriteAction.run(() -> {
            editor.getCaretModel().moveToOffset(location);
            final TemplateManager instance = TemplateManager.getInstance(project);
            instance.startTemplate(editor, IJAdapterHelper.toIJTemplate(project, template));
        });
    }

    @Override
    public void insert(String text, int location) {
        WriteAction.run(() -> {
            editor.getDocument().insertString(location, text);
        });
    }

    @Override
    public void delete(int startLocation, int endLocation) {
        WriteAction.run(() -> {
            editor.getDocument().deleteString(startLocation, endLocation);
        });
    }
}
