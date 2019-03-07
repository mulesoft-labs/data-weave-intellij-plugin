// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WeaveSimpleKeyValuePair extends WeaveKeyValuePair, NavigatablePsiElement {

    @Nullable
  WeaveExpression getExpression();

  @NotNull
  WeaveKey getKey();

  ItemPresentation getPresentation();

}
