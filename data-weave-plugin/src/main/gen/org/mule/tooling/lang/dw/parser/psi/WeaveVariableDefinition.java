// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveVariableDefinition extends WeaveNamedElement {

  @NotNull
  List<WeaveAnnotation> getAnnotationList();

  @NotNull
  WeaveExpression getExpression();

  @NotNull
  WeaveIdentifier getIdentifier();

  @Nullable
  WeaveType getType();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

}
