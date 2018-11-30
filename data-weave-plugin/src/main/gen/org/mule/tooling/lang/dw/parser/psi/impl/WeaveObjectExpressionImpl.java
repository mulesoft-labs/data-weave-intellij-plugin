// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveObjectExpressionImpl extends WeaveExpressionImpl implements WeaveObjectExpression {

  public WeaveObjectExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitObjectExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveMultipleKeyValuePairObj getMultipleKeyValuePairObj() {
    return findChildByClass(WeaveMultipleKeyValuePairObj.class);
  }

  @Override
  @Nullable
  public WeaveSingleKeyValuePairObj getSingleKeyValuePairObj() {
    return findChildByClass(WeaveSingleKeyValuePairObj.class);
  }

  public ItemPresentation getPresentation() {
    return WeavePsiImplUtils.getPresentation(this);
  }

}
