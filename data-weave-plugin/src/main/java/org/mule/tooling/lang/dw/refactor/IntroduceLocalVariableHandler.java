package org.mule.tooling.lang.dw.refactor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;


public class IntroduceLocalVariableHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {

    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }

}
