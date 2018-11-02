// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WeaveAnnotation extends PsiElement {

  @Nullable
  WeaveAnnotationArguments getAnnotationArguments();

  @NotNull
  WeaveFqnIdentifier getFqnIdentifier();

}
