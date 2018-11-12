// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaveSchemaElement extends PsiElement {

  @NotNull
  List<WeaveExpression> getExpressionList();

  @Nullable
  WeaveIdentifier getIdentifier();

}
