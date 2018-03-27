// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface WeaveModuleReference extends PsiElement {

  @NotNull
  WeaveContainerModuleIdentifier getContainerModuleIdentifier();

  @Nullable
  WeaveCustomLoader getCustomLoader();

  @NotNull
  WeaveIdentifier getIdentifier();

  String getPath();

  String getModuleFQN();

  @NotNull
  PsiReference[] getReferences();

}
