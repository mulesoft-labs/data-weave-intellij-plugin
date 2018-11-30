// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveDoExpression extends WeaveExpression {

    @NotNull
    List<WeaveAnnotation> getAnnotationList();

  @NotNull
  List<WeaveDirective> getDirectiveList();

  @Nullable
  WeaveExpression getExpression();

}
