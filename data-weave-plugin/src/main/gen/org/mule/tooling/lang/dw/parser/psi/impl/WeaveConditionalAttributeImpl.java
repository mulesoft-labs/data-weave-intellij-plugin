// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveConditionalAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveSimpleAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveConditionalAttributeImpl extends WeaveAttributeImpl implements WeaveConditionalAttribute {

  public WeaveConditionalAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitConditionalAttribute(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveExpression getExpression() {
      return findChildByClass(WeaveExpression.class);
  }

  @Override
  @NotNull
  public WeaveSimpleAttribute getSimpleAttribute() {
    return findNotNullChildByClass(WeaveSimpleAttribute.class);
  }

}
