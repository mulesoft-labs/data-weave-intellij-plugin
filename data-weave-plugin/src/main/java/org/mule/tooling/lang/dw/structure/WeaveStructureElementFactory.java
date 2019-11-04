package org.mule.tooling.lang.dw.structure;


import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveStructureElementFactory {

    @Nullable
    public static StructureViewTreeElement create(PsiElement element) {
        if (element instanceof WeaveObjectExpression) {
            return new WeaveObjectView((WeaveObjectExpression) element);
        } else if (element instanceof WeaveArrayExpression) {
            return new WeaveArrayView((WeaveArrayExpression) element);
        } else if (element instanceof WeaveAnnotationDirective) {
            return new WeaveAnnotationView((WeaveAnnotationDirective) element);
        } else if (element instanceof WeaveBinaryExpression) {
            return create(element.getLastChild());
        } else if (element instanceof WeaveUsingExpression) {
            return create(((WeaveUsingExpression) element).getExpression());
        } else if (element instanceof WeaveFunctionDirective) {
            return new WeaveFunctionDirectiveView((WeaveFunctionDirective) element);
        } else if (element instanceof WeaveVariableDirective) {
            return new WeaveVariableDirectiveView((WeaveVariableDirective) element);
        } else if (element instanceof WeaveTypeDirective) {
            return new WeaveTypeDirectiveView((WeaveTypeDirective) element);
        } else {
            return null;
        }
    }
}
