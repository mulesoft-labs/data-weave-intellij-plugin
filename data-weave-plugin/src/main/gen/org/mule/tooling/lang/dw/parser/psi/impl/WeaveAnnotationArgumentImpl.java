// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotationArgument;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveAnnotationArgumentImpl extends ASTWrapperPsiElement implements WeaveAnnotationArgument {

  public WeaveAnnotationArgumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitAnnotationArgument(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof WeaveVisitor) accept((WeaveVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public WeaveExpression getExpression() {
    return findNotNullChildByClass(WeaveExpression.class);
  }

  @Override
  @NotNull
  public WeaveIdentifier getIdentifier() {
    return findNotNullChildByClass(WeaveIdentifier.class);
  }

}
