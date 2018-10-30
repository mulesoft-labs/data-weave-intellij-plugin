// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveAttributes;
import org.mule.tooling.lang.dw.parser.psi.WeaveKey;
import org.mule.tooling.lang.dw.parser.psi.WeaveQualifiedName;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveKeyImpl extends ASTWrapperPsiElement implements WeaveKey {

  public WeaveKeyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitKey(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveAttributes getAttributes() {
    return findChildByClass(WeaveAttributes.class);
  }

  @Override
  @NotNull
  public WeaveQualifiedName getQualifiedName() {
    return findNotNullChildByClass(WeaveQualifiedName.class);
  }

}
