// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface WeaveNamedTypePattern extends WeavePattern, WeaveNamedElement {

  @NotNull
  WeaveExpression getExpression();

  @NotNull
  WeaveIdentifier getIdentifier();

  @NotNull
  WeaveType getType();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

}
