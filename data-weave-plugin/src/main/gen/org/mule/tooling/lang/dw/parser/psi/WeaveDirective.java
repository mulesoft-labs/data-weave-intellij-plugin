// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WeaveDirective extends PsiElement {

  @NotNull
  List<WeaveAnnotation> getAnnotationList();

  @NotNull
  WeaveDirective getDirective();

}
