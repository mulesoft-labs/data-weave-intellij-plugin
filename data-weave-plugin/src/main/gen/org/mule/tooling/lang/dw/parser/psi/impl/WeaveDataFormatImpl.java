// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDataFormat;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

import static org.mule.tooling.lang.dw.parser.psi.WeaveTypes.MIME_TYPE_KEYWORD;

public class WeaveDataFormatImpl extends ASTWrapperPsiElement implements WeaveDataFormat {

  public WeaveDataFormatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitDataFormat(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getMimeTypeKeyword() {
    return findNotNullChildByType(MIME_TYPE_KEYWORD);
  }

}
