// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveDeclaredNamespace;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveMultiValueSelector;
import org.mule.tooling.lang.dw.parser.psi.WeaveStringLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveMultiValueSelectorImpl extends ASTWrapperPsiElement implements WeaveMultiValueSelector {

  public WeaveMultiValueSelectorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitMultiValueSelector(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveDeclaredNamespace getDeclaredNamespace() {
    return findChildByClass(WeaveDeclaredNamespace.class);
  }

  @Override
  @Nullable
  public WeaveIdentifier getIdentifier() {
    return findChildByClass(WeaveIdentifier.class);
  }

  @Override
  @Nullable
  public WeaveStringLiteral getStringLiteral() {
    return findChildByClass(WeaveStringLiteral.class);
  }

}
