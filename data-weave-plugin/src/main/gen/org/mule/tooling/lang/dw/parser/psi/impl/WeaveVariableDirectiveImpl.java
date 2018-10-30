// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotation;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveVariableDirectiveImpl extends WeaveDirectiveImpl implements WeaveVariableDirective {

  public WeaveVariableDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitVariableDirective(this);
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
  @Nullable
  public WeaveVariableDefinition getVariableDefinition() {
    return findChildByClass(WeaveVariableDefinition.class);
  }

}
