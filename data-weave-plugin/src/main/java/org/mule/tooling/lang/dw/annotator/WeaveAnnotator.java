package org.mule.tooling.lang.dw.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.highlighter.WeaveSyntaxHighlighter;
import org.mule.tooling.lang.dw.parser.psi.WeaveBinaryExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDefinition;


public class WeaveAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

    if (element instanceof WeaveIdentifier && element.getParent() instanceof WeaveFunctionDefinition) {
      holder.createInfoAnnotation(element, "Function").setTextAttributes(WeaveSyntaxHighlighter.FUNCTION_DECLARATION);
    }
    if (element instanceof WeaveIdentifier && element.getParent() instanceof WeaveVariableDefinition) {
      holder.createInfoAnnotation(element, "Variable").setTextAttributes(WeaveSyntaxHighlighter.VARIABLE);
    }
    if (element instanceof WeaveIdentifier && element.getParent() instanceof WeaveFunctionCallExpression) {
      holder.createInfoAnnotation(element, "Function call").setTextAttributes(WeaveSyntaxHighlighter.FUNCTION_CALL);
    }
    if (element instanceof WeaveBinaryExpression) {
      holder.createInfoAnnotation(((WeaveBinaryExpression) element).getBinaryFunctionIdentifier(), "Infix Function Call")
              .setTextAttributes(WeaveSyntaxHighlighter.INFIX_FUNCTION_CALL);
    }
  }
}
