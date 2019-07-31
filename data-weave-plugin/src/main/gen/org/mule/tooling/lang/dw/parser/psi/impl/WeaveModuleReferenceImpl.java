// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveModuleReferenceImpl extends ASTWrapperPsiElement implements WeaveModuleReference {

  public WeaveModuleReferenceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitModuleReference(this);
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

    @Override
    public String getPath() {
        return WeavePsiImplUtils.getPath(this);
    }

    @Override
    public String getModuleFQN() {
        return WeavePsiImplUtils.getModuleFQN(this);
    }

    @Override
    @NotNull
    public PsiReference[] getReferences() {
        return WeavePsiImplUtils.getReferences(this);
    }

}
