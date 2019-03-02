package org.mule.tooling.lang.dw.structure;


import com.intellij.icons.AllIcons;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionParameter;
import org.mule.tooling.lang.dw.parser.psi.WeaveType;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WeaveFunctionDirectiveView extends PsiTreeElementBase<WeaveFunctionDirective> {
  protected WeaveFunctionDirectiveView(WeaveFunctionDirective psiElement) {
    super(psiElement);
  }

  @NotNull
  @Override
  public Collection<StructureViewTreeElement> getChildrenBase() {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public String getPresentableText() {
    WeaveFunctionDefinition functionDefinition = getElement().getFunctionDefinition();
    List<WeaveFunctionParameter> functionParameterList = functionDefinition.getFunctionParameterList();
    StringBuilder params = new StringBuilder();
    for (WeaveFunctionParameter weaveFunctionParameter : functionParameterList) {
        String variableName = weaveFunctionParameter.getNameIdentifier().getText();
      WeaveType type = weaveFunctionParameter.getType();
      if (type != null) {
        variableName = variableName + ": " + type.getText();
      }
      if (params.length() == 0) {
        params.append(variableName);
      } else {
        params.append(", ").append(variableName);
      }
    }
    String functionDescription = functionDefinition.getName() + "(" + params + ")";
    if (functionDefinition.getType() != null) {
      functionDescription = functionDescription + ": " + functionDefinition.getType().getText();
    }
    return functionDescription;
  }

  @Override
  public Icon getIcon(boolean open) {
    return AllIcons.Nodes.Function;
  }
}
