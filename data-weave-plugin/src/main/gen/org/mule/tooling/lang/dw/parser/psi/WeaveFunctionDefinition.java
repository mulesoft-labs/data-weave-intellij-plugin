// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveFunctionDefinition extends WeaveNamedElement {

  @Nullable
  WeaveDynamicReturn getDynamicReturn();

  @Nullable
  WeaveExpression getExpression();

  @NotNull
  List<WeaveFunctionParameter> getFunctionParameterList();

  @NotNull
  WeaveIdentifier getIdentifier();

  @Nullable
  WeaveType getType();

  @NotNull
  List<WeaveTypeParameter> getTypeParameterList();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

}
