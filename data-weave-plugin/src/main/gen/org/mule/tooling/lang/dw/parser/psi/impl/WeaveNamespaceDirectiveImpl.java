// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamespaceDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamespaceDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveNamespaceDirectiveImpl extends WeaveDirectiveImpl implements WeaveNamespaceDirective {

  public WeaveNamespaceDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitNamespaceDirective(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveNamespaceDefinition getNamespaceDefinition() {
    return findChildByClass(WeaveNamespaceDefinition.class);
  }

}
