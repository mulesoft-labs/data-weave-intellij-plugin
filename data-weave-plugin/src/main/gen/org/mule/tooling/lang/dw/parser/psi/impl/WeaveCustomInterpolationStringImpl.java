// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveCustomInterpolationString;
import org.mule.tooling.lang.dw.parser.psi.WeaveStringLiteralMixin;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import static org.mule.tooling.lang.dw.parser.psi.WeaveTypes.BACKTIKED_QUOTED_STRING;

public class WeaveCustomInterpolationStringImpl extends WeaveStringLiteralMixin implements WeaveCustomInterpolationString {

  public WeaveCustomInterpolationStringImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitCustomInterpolationString(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getBacktikedQuotedString() {
    return findNotNullChildByType(BACKTIKED_QUOTED_STRING);
  }

}
