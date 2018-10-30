// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFqnIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveReferenceType;
import org.mule.tooling.lang.dw.parser.psi.WeaveSchema;
import org.mule.tooling.lang.dw.parser.psi.WeaveType;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveReferenceTypeImpl extends WeaveTypeImpl implements WeaveReferenceType {

  public WeaveReferenceTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitReferenceType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveFqnIdentifier getFqnIdentifier() {
    return findNotNullChildByClass(WeaveFqnIdentifier.class);
  }

  @Override
  @Nullable
  public WeaveSchema getSchema() {
    return findChildByClass(WeaveSchema.class);
  }

  @Override
  @NotNull
  public List<WeaveType> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveType.class);
  }

}
