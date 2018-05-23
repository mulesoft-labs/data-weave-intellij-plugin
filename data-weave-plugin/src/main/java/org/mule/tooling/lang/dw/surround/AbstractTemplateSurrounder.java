package org.mule.tooling.lang.dw.surround;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.refactoring.introduceField.ElementToWorkOn;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTemplateSurrounder implements Surrounder {
    @Override
    public boolean isApplicable(@NotNull PsiElement[] elements) {
        return elements.length > 0 && !(elements[0] instanceof LeafPsiElement || elements[0] instanceof PsiWhiteSpace);
    }

    @Nullable
    @Override
    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements) throws IncorrectOperationException {
        if (elements.length > 0) {
            PsiElement expr = elements[0];
            final Template template = createTemplate(project, expr);
            TextRange range;
            if (expr.isPhysical()) {
                range = expr.getTextRange();
            } else {
                final RangeMarker rangeMarker = expr.getUserData(ElementToWorkOn.TEXT_RANGE);
                if (rangeMarker == null) return null;
                range = new TextRange(rangeMarker.getStartOffset(), rangeMarker.getEndOffset());
            }
            editor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());
            editor.getCaretModel().moveToOffset(range.getStartOffset());
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            TemplateManager.getInstance(project).startTemplate(editor, template);
        }
        return null;
    }

    public abstract Template createTemplate(@NotNull Project project, PsiElement expr);
}
