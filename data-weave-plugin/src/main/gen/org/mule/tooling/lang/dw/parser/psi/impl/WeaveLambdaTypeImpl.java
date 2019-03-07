// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveLambdaType;
import org.mule.tooling.lang.dw.parser.psi.WeaveLambdaTypeParameter;
import org.mule.tooling.lang.dw.parser.psi.WeaveType;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveLambdaTypeImpl extends WeaveTypeImpl implements WeaveLambdaType {

  public WeaveLambdaTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitLambdaType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeaveLambdaTypeParameter> getLambdaTypeParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveLambdaTypeParameter.class);
  }

  @Override
  @Nullable
  public WeaveType getType() {
      return findChildByClass(WeaveType.class);
  }

}
