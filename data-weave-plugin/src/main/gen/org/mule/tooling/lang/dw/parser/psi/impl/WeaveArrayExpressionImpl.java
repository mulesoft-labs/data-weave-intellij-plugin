// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveArrayExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiImplUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveArrayExpressionImpl extends WeaveExpressionImpl implements WeaveArrayExpression {

  public WeaveArrayExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitArrayExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeaveExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveExpression.class);
  }

    @Override
    public ItemPresentation getPresentation() {
        return WeavePsiImplUtils.getPresentation(this);
    }

}
