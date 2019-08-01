// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface WeaveFqnIdentifier extends WeaveNamedElement {

  @NotNull
  WeaveContainerModuleIdentifier getContainerModuleIdentifier();

  @Nullable
  WeaveCustomLoader getCustomLoader();

  @NotNull
  WeaveIdentifier getIdentifier();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

  PsiReference[] getReferences();

}
