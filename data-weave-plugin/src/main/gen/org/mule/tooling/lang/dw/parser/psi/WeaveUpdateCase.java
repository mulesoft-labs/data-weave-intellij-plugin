// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeaveUpdateCase extends PsiElement {

  @Nullable
  WeaveAttributeSelector getAttributeSelector();

  @NotNull
  List<WeaveExpression> getExpressionList();

  @NotNull
  List<WeaveIdentifier> getIdentifierList();

  @Nullable
  WeaveMultiValueSelector getMultiValueSelector();

  @Nullable
  WeaveValueSelector getValueSelector();

}
