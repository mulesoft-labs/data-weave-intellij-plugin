// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveTypeDirectiveImpl extends WeaveDirectiveImpl implements WeaveTypeDirective {

  public WeaveTypeDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitTypeDirective(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveTypeDefinition getTypeDefinition() {
    return findChildByClass(WeaveTypeDefinition.class);
  }

}
