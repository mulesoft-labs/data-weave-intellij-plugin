// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.mule.tooling.lang.dw.parser.psi.WeaveTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.mule.tooling.lang.dw.parser.psi.*;
import com.intellij.navigation.ItemPresentation;
import javax.swing.Icon;

public class WeaveDocumentImpl extends ASTWrapperPsiElement implements WeaveDocument {

  public WeaveDocumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDocument(this);
  }

  @Override
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
  public @NotNull String getQualifiedName() {
    return WeavePsiImplUtils.getQualifiedName(this);
  }

  @Override
  public @NotNull String getName() {
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

  @Override
  public Icon getElementIcon(int flags) {
    return WeavePsiImplUtils.getElementIcon(this, flags);
  }

  @Override
  public @Nullable WeaveOutputDirective getOutput() {
    return WeavePsiImplUtils.getOutput(this);
  }

}
