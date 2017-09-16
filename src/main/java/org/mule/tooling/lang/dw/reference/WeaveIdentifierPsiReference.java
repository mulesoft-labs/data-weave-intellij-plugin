package org.mule.tooling.lang.dw.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeaveIdentifierPsiReference extends PsiReferenceBase<PsiElement> {
  private String variableName;

  public WeaveIdentifierPsiReference(@NotNull WeaveNamedElement element) {
    super(element, TextRange.from(element.getNameIdentifier().getStartOffsetInParent(), element.getNameIdentifier().getTextLength()));
    variableName = element.getNameIdentifier().getText();
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    if (variableName.equals("$") || variableName.equals("$$")) {
      return WeavePsiUtils.findImplicitVariable(myElement);
    } else {
      Optional<? extends PsiElement> variables = WeavePsiUtils.getVariableDeclarationFor(myElement, variableName);
      if (variables.isPresent()) {
        return variables.get();
      } else {
        Optional<? extends PsiElement> function = WeavePsiUtils.findFunction(myElement, variableName);
        return function.orElse(null);
      }
    }
  }


  @NotNull
  @Override
  public Object[] getVariants() {
    final List<WeaveVariable> variables = WeavePsiUtils.collectLocalVisibleVariables(myElement);
    final List<LookupElement> variants = new ArrayList<>();
    for (final WeaveVariable property : variables) {
      if (property.getName() != null && property.getName().length() > 0) {
        variants.add(LookupElementBuilder.create(property).
                withIcon(AllIcons.Nodes.Variable).
                withTypeText(property.getContainingFile().getName())
        );
      }
    }
    return variants.toArray();
  }
}
