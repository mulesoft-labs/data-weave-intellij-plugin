// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

import java.util.List;

public class WeaveDoExpressionImpl extends WeaveExpressionImpl implements WeaveDoExpression {

  public WeaveDoExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDoExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

    @Override
    @NotNull
    public List<WeaveAnnotation> getAnnotationList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveAnnotation.class);
    }

  @Override
  @NotNull
  public List<WeaveDirective> getDirectiveList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveDirective.class);
  }

  @Override
  @Nullable
  public WeaveExpression getExpression() {
    return findChildByClass(WeaveExpression.class);
  }

}
