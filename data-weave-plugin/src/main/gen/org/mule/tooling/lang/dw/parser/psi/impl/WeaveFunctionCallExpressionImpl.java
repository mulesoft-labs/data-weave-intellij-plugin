// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallArguments;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveFunctionCallExpressionImpl extends WeaveExpressionImpl implements WeaveFunctionCallExpression {

  public WeaveFunctionCallExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitFunctionCallExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveExpression getExpression() {
    return findNotNullChildByClass(WeaveExpression.class);
  }

  @Override
  @NotNull
  public WeaveFunctionCallArguments getFunctionCallArguments() {
    return findNotNullChildByClass(WeaveFunctionCallArguments.class);
  }

}
