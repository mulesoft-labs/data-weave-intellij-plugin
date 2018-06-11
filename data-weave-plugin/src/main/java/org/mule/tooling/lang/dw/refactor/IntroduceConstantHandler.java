package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;
import org.mule.weave.v2.editor.VariableDependency;
import org.mule.weave.v2.scope.VariableScope;

public class IntroduceConstantHandler extends IntroduceLocalVariableHandler {

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext, PsiElement valueToReplace) {
        WeaveEditorToolingAPI toolingAPI = WeaveEditorToolingAPI.getInstance(project);
        VariableScope variableScope = toolingAPI.scopeOf(getTargetScope(psiFile, valueToReplace));
        VariableDependency[] variableDependencies = toolingAPI.externalScopeDependencies(valueToReplace, variableScope);
        if (variableDependencies.length > 0) {
            HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Selected expression can not be moved to the root scope.");
        } else {
            super.invoke(project, editor, psiFile, dataContext, valueToReplace);
        }
    }

    @Override
    public PsiElement getTargetScope(PsiFile psiFile, PsiElement valueToReplace) {
        return WeavePsiUtils.getWeaveDocument(psiFile);
    }
}
