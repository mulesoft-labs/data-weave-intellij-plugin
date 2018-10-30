// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveQualifiedName;
import org.mule.tooling.lang.dw.parser.psi.WeaveSimpleAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveSimpleAttributeImpl extends WeaveAttributeImpl implements WeaveSimpleAttribute {

  public WeaveSimpleAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitSimpleAttribute(this);
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
  public WeaveQualifiedName getQualifiedName() {
    return findNotNullChildByClass(WeaveQualifiedName.class);
  }

}
