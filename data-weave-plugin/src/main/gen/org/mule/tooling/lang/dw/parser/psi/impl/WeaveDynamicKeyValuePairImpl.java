// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDynamicKeyValuePair;
import org.mule.tooling.lang.dw.parser.psi.WeaveEnclosedExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveDynamicKeyValuePairImpl extends WeaveKeyValuePairImpl implements WeaveDynamicKeyValuePair {

  public WeaveDynamicKeyValuePairImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDynamicKeyValuePair(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveEnclosedExpression getEnclosedExpression() {
    return findNotNullChildByClass(WeaveEnclosedExpression.class);
  }

}
