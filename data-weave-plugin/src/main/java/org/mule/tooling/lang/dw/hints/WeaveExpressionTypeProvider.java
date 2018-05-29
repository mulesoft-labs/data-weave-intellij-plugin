package org.mule.tooling.lang.dw.hints;

import com.intellij.lang.ExpressionTypeProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.weave.v2.ts.WeaveType;

import java.util.Collections;
import java.util.List;

public class WeaveExpressionTypeProvider extends ExpressionTypeProvider {
    @NotNull
    @Override
    public String getInformationHint(@NotNull PsiElement element) {
        WeaveType weaveType = DWEditorToolingAPI.getInstance(element.getProject()).typeOf(element);
        if (weaveType != null) {
            return weaveType.toString(false, true);
        } else {
            return "Invalid expression selection";
        }
    }

    @NotNull
    @Override
    public String getErrorHint() {
        return "Invalid expression selection";
    }

    @NotNull
    @Override
    public List getExpressionsAt(@NotNull PsiElement elementAt) {
        if (elementAt instanceof PsiWhiteSpace) {
            return Collections.emptyList();
        } else {
            PsiElement expression = elementAt;
            while (expression instanceof LeafPsiElement) {
                expression = expression.getParent();
            }
            return Collections.singletonList(expression);
        }
    }
}
