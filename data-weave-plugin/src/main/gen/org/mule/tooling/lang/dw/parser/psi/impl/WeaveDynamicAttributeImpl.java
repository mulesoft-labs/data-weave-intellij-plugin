// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDynamicAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveEnclosedExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveDynamicAttributeImpl extends WeaveAttributeImpl implements WeaveDynamicAttribute {

  public WeaveDynamicAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDynamicAttribute(this);
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
