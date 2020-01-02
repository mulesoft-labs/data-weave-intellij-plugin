// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveInputDirective extends WeaveDirective {

  @Nullable
  WeaveDataFormat getDataFormat();

  @NotNull
  List<WeaveIdentifier> getIdentifierList();

  @Nullable
  WeaveOptions getOptions();

  @Nullable
  WeaveType getType();

}
