// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeaveTypeDirective extends WeaveDirective, WeaveNamedElement {

  @Nullable
  WeaveIdentifier getIdentifier();

  @Nullable
  WeaveType getType();

  @NotNull
  List<WeaveTypeParameter> getTypeParameterList();

  @Nullable
  WeaveUndefinedLiteral getUndefinedLiteral();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

}
