package org.mule.tooling.runtime.schema;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class MuleCompletionContributor extends CompletionContributor {

  public MuleCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement(),
        new MuleCompletionProvider());
  }
}
