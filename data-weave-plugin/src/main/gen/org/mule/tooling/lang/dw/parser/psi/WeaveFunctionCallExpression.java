// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeaveFunctionCallExpression extends WeaveExpression {

  @NotNull
  WeaveExpression getExpression();

  @NotNull
  WeaveFunctionCallArguments getFunctionCallArguments();

  @Nullable
  WeaveFunctionCallTypeParameters getFunctionCallTypeParameters();

}
