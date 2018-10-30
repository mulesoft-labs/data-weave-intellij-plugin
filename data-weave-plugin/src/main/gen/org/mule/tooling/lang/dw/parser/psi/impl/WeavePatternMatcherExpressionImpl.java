// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeavePattern;
import org.mule.tooling.lang.dw.parser.psi.WeavePatternMatcherExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeavePatternMatcherExpressionImpl extends WeaveExpressionImpl implements WeavePatternMatcherExpression {

  public WeavePatternMatcherExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitPatternMatcherExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeavePattern> getPatternList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeavePattern.class);
  }

}
