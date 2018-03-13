package org.mule.tooling.lang.dw.qn;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class WeaveQualifiedNameProvider implements QualifiedNameProvider {
    @Nullable
    @Override
    public PsiElement adjustElementToCopy(PsiElement psiElement) {
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName(PsiElement psiElement) {
        return null;
    }

    @Override
    public PsiElement qualifiedNameToElement(String s, Project project) {
        return null;
    }

    @Override
    public void insertQualifiedName(String s, PsiElement psiElement, Editor editor, Project project) {

    }
}
