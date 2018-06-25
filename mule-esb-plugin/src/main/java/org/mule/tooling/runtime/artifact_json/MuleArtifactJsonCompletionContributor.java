package org.mule.tooling.runtime.artifact_json;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.mule.runtime.api.deployment.meta.MuleApplicationModel;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class MuleArtifactJsonCompletionContributor extends CompletionContributor {

  public MuleArtifactJsonCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement(),
        new ClassBasedCompletionContributor(MuleApplicationModel.class, (file) -> file.getName().equals("mule-artifact.json")));
  }
}
