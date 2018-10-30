// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveLiteralExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveSchemaElement;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveSchemaElementImpl extends ASTWrapperPsiElement implements WeaveSchemaElement {

  public WeaveSchemaElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitSchemaElement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveIdentifier getIdentifier() {
    return findChildByClass(WeaveIdentifier.class);
  }

  @Override
  @NotNull
  public List<WeaveLiteralExpression> getLiteralExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveLiteralExpression.class);
  }

}
