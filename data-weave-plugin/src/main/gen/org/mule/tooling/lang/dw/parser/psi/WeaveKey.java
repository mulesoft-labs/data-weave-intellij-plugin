// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeaveKey extends PsiElement {

  @NotNull
  List<WeaveAnnotation> getAnnotationList();

  @Nullable
  WeaveAttributes getAttributes();

  @NotNull
  WeaveQualifiedName getQualifiedName();

}
