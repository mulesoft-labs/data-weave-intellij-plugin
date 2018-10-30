// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveNameType;
import org.mule.tooling.lang.dw.parser.psi.WeaveStringLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeParameter;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveNameTypeImpl extends WeaveTypeImpl implements WeaveNameType {

  public WeaveNameTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitNameType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeaveIdentifier> getIdentifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveIdentifier.class);
  }

  @Override
  @Nullable
  public WeaveStringLiteral getStringLiteral() {
    return findChildByClass(WeaveStringLiteral.class);
  }

  @Override
  @Nullable
  public WeaveTypeParameter getTypeParameter() {
    return findChildByClass(WeaveTypeParameter.class);
  }

}
