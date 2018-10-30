// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveCloseOrderedObjectType;
import org.mule.tooling.lang.dw.parser.psi.WeaveKeyValuePairType;
import org.mule.tooling.lang.dw.parser.psi.WeaveSchema;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveCloseOrderedObjectTypeImpl extends WeaveTypeImpl implements WeaveCloseOrderedObjectType {

  public WeaveCloseOrderedObjectTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitCloseOrderedObjectType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeaveKeyValuePairType> getKeyValuePairTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveKeyValuePairType.class);
  }

  @Override
  @Nullable
  public WeaveSchema getSchema() {
    return findChildByClass(WeaveSchema.class);
  }

}
