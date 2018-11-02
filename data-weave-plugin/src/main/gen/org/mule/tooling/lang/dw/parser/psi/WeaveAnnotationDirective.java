// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveAnnotationDirective extends WeaveDirective {

  @NotNull
  List<WeaveAnnotation> getAnnotationList();

  @NotNull
  List<WeaveAnnotationParameter> getAnnotationParameterList();

  @Nullable
  WeaveIdentifier getIdentifier();

}
