package org.mule.tooling.lang.dw.structure;


import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveArrayExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveBinaryExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveObjectExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveUsingExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;

public class WeaveStructureElementFactory {

  @Nullable
  public static StructureViewTreeElement create(PsiElement element) {
    if (element instanceof WeaveObjectExpression) {
      return new WeaveObjectView((WeaveObjectExpression) element);
    } else if (element instanceof WeaveArrayExpression) {
      return new WeaveArrayView((WeaveArrayExpression) element);
    } else if (element instanceof WeaveBinaryExpression) {
      return create(((WeaveBinaryExpression) element).getRight());
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
