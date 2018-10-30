// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveFqnIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableReferenceExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveVariableReferenceExpressionImpl extends WeaveExpressionImpl implements WeaveVariableReferenceExpression {

  public WeaveVariableReferenceExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitVariableReferenceExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveFqnIdentifier getFqnIdentifier() {
    return findNotNullChildByClass(WeaveFqnIdentifier.class);
  }

}
