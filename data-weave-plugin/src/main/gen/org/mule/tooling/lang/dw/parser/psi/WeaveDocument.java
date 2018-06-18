// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WeaveDocument extends NavigatablePsiElement, PsiQualifiedNamedElement {

  @Nullable
  WeaveBody getBody();

  @Nullable
  WeaveHeader getHeader();

  ItemPresentation getPresentation();

  @NotNull
  String getQualifiedName();

  @NotNull
  String getName();

  WeaveDocument setName(String name);

  boolean isMappingDocument();

  boolean isModuleDocument();

}
