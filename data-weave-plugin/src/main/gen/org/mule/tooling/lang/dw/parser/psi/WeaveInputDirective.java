// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveInputDirective extends WeaveDirective {

  @NotNull
  List<WeaveAnnotation> getAnnotationList();

  @Nullable
  WeaveDataFormat getDataFormat();

  @Nullable
  WeaveIdentifier getIdentifier();

  @Nullable
  WeaveOptions getOptions();

  @Nullable
  WeaveType getType();

}
