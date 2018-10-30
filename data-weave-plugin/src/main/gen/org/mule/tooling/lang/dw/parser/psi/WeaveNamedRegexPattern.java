// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WeaveNamedRegexPattern extends WeavePattern, WeaveNamedElement {

  @NotNull
  List<WeaveExpression> getExpressionList();

  @NotNull
  WeaveIdentifier getIdentifier();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

}
