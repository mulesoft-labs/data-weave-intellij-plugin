// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

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
  @Nullable
  public WeaveVariableDefinition getVariableDefinition() {
    return findChildByClass(WeaveVariableDefinition.class);
  }

}
