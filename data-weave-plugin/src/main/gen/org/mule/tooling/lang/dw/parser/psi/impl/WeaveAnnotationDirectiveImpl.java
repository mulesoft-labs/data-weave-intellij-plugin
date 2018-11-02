// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotation;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotationDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotationParameter;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import java.util.List;

public class WeaveAnnotationDirectiveImpl extends WeaveDirectiveImpl implements WeaveAnnotationDirective {

  public WeaveAnnotationDirectiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitAnnotationDirective(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WeaveAnnotation> getAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveAnnotation.class);
  }

  @Override
  @NotNull
  public List<WeaveAnnotationParameter> getAnnotationParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveAnnotationParameter.class);
  }

  @Override
  @Nullable
  public WeaveIdentifier getIdentifier() {
    return findChildByClass(WeaveIdentifier.class);
  }

}
