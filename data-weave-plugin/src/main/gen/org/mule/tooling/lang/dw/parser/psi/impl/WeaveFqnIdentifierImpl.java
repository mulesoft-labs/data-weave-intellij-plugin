// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveFqnIdentifierImpl extends WeaveNamedElementImpl implements WeaveFqnIdentifier {

  public WeaveFqnIdentifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitFqnIdentifier(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveContainerModuleIdentifier getContainerModuleIdentifier() {
    return findNotNullChildByClass(WeaveContainerModuleIdentifier.class);
  }

  @Override
  @Nullable
  public WeaveCustomLoader getCustomLoader() {
    return findChildByClass(WeaveCustomLoader.class);
  }

  @Override
  @NotNull
  public WeaveIdentifier getIdentifier() {
    return findNotNullChildByClass(WeaveIdentifier.class);
  }

  public PsiReference[] getReferences() {
    return WeavePsiImplUtils.getReferences(this);
  }

}
