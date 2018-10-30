// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveLiteralExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveOptionElement;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveOptionElementImpl extends ASTWrapperPsiElement implements WeaveOptionElement {

  public WeaveOptionElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitOptionElement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveIdentifier getIdentifier() {
    return findNotNullChildByClass(WeaveIdentifier.class);
  }

  @Override
  @NotNull
  public WeaveLiteralExpression getLiteralExpression() {
    return findNotNullChildByClass(WeaveLiteralExpression.class);
  }

}
