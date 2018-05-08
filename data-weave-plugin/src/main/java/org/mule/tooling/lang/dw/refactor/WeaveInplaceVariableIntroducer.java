package org.mule.tooling.lang.dw.refactor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;

public class WeaveInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {

    public WeaveInplaceVariableIntroducer(PsiNamedElement elementToRename, Editor editor, Project project, String title, PsiElement[] occurrences) {
        super(elementToRename, editor, project, title, occurrences, null);
    }

    @Nullable
    @Override
    protected PsiElement checkLocalScope() {
        return WeavePsiUtils.getWeaveDocument(myElementToRename.getContainingFile());
    }
}
