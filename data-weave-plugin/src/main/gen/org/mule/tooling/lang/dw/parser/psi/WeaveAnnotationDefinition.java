// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import javax.swing.Icon;

public interface WeaveAnnotationDefinition extends WeaveNamedElement {

  @NotNull
  List<WeaveAnnotationParameter> getAnnotationParameterList();

  @NotNull
  WeaveIdentifier getIdentifier();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

  Icon getElementIcon(int flags);

}
