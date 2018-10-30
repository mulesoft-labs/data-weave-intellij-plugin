// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveKeyValuePair;
import org.mule.tooling.lang.dw.parser.psi.WeaveSingleKeyValuePairObj;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveSingleKeyValuePairObjImpl extends ASTWrapperPsiElement implements WeaveSingleKeyValuePairObj {

  public WeaveSingleKeyValuePairObjImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitSingleKeyValuePairObj(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveKeyValuePair getKeyValuePair() {
    return findNotNullChildByClass(WeaveKeyValuePair.class);
  }

}
