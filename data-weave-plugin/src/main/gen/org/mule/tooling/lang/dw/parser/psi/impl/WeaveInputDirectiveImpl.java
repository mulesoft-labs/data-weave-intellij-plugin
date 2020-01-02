// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

import java.util.List;

public class WeaveInputDirectiveImpl extends WeaveDirectiveImpl implements WeaveInputDirective {

  public WeaveInputDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitInputDirective(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveDataFormat getDataFormat() {
    return findChildByClass(WeaveDataFormat.class);
  }

  @Override
  @NotNull
  public List<WeaveIdentifier> getIdentifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveIdentifier.class);
  }

  @Override
  @Nullable
  public WeaveOptions getOptions() {
    return findChildByClass(WeaveOptions.class);
  }

  @Override
  @Nullable
  public WeaveType getType() {
    return findChildByClass(WeaveType.class);
  }

}
