// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.navigation.ItemPresentation;
import javax.swing.Icon;

public interface WeaveDocument extends NavigatablePsiElement, PsiQualifiedNamedElement {

  @Nullable
  WeaveBody getBody();

  @Nullable
  WeaveHeader getHeader();

  ItemPresentation getPresentation();

  @NotNull String getQualifiedName();

  @NotNull String getName();

  WeaveDocument setName(String name);

  boolean isMappingDocument();

  boolean isModuleDocument();

  Icon getElementIcon(int flags);

  @Nullable WeaveOutputDirective getOutput();

}
