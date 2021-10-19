package org.mule.tooling.restsdk.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RestSdkCompletionContributor extends CompletionContributor {
  public RestSdkCompletionContributor() {
    extend(
            CompletionType.BASIC,
            psiElement(),
            new RestSdkCompletionProvider());
  }
}
