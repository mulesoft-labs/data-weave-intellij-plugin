// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveDocumentImpl extends ASTWrapperPsiElement implements WeaveDocument {

  public WeaveDocumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDocument(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveBody getBody() {
    return findChildByClass(WeaveBody.class);
  }

  @Override
  @Nullable
  public WeaveHeader getHeader() {
    return findChildByClass(WeaveHeader.class);
  }

    @Override
    public ItemPresentation getPresentation() {
        return WeavePsiImplUtils.getPresentation(this);
    }

    @Override
    @NotNull
    public String getQualifiedName() {
        return WeavePsiImplUtils.getQualifiedName(this);
    }

    @Override
    @NotNull
    public String getName() {
        return WeavePsiImplUtils.getName(this);
    }

    @Override
    public WeaveDocument setName(String name) {
        return WeavePsiImplUtils.setName(this, name);
    }

    @Override
    public boolean isMappingDocument() {
        return WeavePsiImplUtils.isMappingDocument(this);
    }

    @Override
    public boolean isModuleDocument() {
        return WeavePsiImplUtils.isModuleDocument(this);
    }

}
