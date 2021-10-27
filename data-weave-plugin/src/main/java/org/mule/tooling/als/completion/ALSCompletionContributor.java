package org.mule.tooling.als.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ALSCompletionContributor extends CompletionContributor {

  public ALSCompletionContributor() {
    extend(
            CompletionType.BASIC,
            psiElement(),
            new ALSCompletionProvider());
  }
}
