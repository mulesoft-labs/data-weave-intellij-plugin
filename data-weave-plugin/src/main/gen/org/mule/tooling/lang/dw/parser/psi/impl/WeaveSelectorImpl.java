// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

public class WeaveSelectorImpl extends ASTWrapperPsiElement implements WeaveSelector {

  public WeaveSelectorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitSelector(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

    @Override
    @Nullable
    public WeaveAllAttributeSelector getAllAttributeSelector() {
        return findChildByClass(WeaveAllAttributeSelector.class);
    }

    @Override
    @Nullable
    public WeaveAllSchemaSelector getAllSchemaSelector() {
        return findChildByClass(WeaveAllSchemaSelector.class);
    }

  @Override
  @Nullable
  public WeaveAttributeSelector getAttributeSelector() {
    return findChildByClass(WeaveAttributeSelector.class);
  }

  @Override
  @Nullable
  public WeaveMultiValueSelector getMultiValueSelector() {
    return findChildByClass(WeaveMultiValueSelector.class);
  }

  @Override
  @Nullable
  public WeaveNamespaceSelector getNamespaceSelector() {
    return findChildByClass(WeaveNamespaceSelector.class);
  }

  @Override
  @Nullable
  public WeaveObjectSelector getObjectSelector() {
    return findChildByClass(WeaveObjectSelector.class);
  }

  @Override
  @Nullable
  public WeaveSchemaSelector getSchemaSelector() {
    return findChildByClass(WeaveSchemaSelector.class);
  }

  @Override
  @Nullable
  public WeaveValueSelector getValueSelector() {
    return findChildByClass(WeaveValueSelector.class);
  }

}
