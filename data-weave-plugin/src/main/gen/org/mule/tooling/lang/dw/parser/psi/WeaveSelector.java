// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeaveSelector extends PsiElement {

  @Nullable
  WeaveAllAttributeSelector getAllAttributeSelector();

  @Nullable
  WeaveAllSchemaSelector getAllSchemaSelector();

  @Nullable
  WeaveAttributeMultiValueSelector getAttributeMultiValueSelector();

  @Nullable
  WeaveAttributeSelector getAttributeSelector();

  @Nullable
  WeaveMultiValueSelector getMultiValueSelector();

  @Nullable
  WeaveNamespaceSelector getNamespaceSelector();

  @Nullable
  WeaveObjectSelector getObjectSelector();

  @Nullable
  WeaveSchemaSelector getSchemaSelector();

  @Nullable
  WeaveValueSelector getValueSelector();

}
