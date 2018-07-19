// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface WeaveCustomInterpolatorExpression extends WeaveExpression, WeaveNamedElement {

  @NotNull
  WeaveCustomInterpolationString getCustomInterpolationString();

  @NotNull
  WeaveIdentifier getIdentifier();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

}
