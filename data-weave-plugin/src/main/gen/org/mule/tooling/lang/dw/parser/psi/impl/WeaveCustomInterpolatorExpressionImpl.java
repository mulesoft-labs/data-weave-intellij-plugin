// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveCustomInterpolationString;
import org.mule.tooling.lang.dw.parser.psi.WeaveCustomInterpolatorExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElementImpl;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveCustomInterpolatorExpressionImpl extends WeaveNamedElementImpl implements WeaveCustomInterpolatorExpression {

  public WeaveCustomInterpolatorExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitCustomInterpolatorExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveCustomInterpolationString getCustomInterpolationString() {
    return findNotNullChildByClass(WeaveCustomInterpolationString.class);
  }

  @Override
  @NotNull
  public WeaveIdentifier getIdentifier() {
    return findNotNullChildByClass(WeaveIdentifier.class);
  }

}
