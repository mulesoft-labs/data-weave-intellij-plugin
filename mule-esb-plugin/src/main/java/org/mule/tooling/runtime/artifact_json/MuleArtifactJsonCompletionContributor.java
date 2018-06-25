package org.mule.tooling.runtime.artifact_json;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.mule.runtime.api.deployment.meta.AbstractMuleArtifactModel;
import org.mule.tooling.runtime.util.MuleModuleUtils;
import org.mule.tooling.runtime.util.ProjectType;

import java.util.Optional;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class MuleArtifactJsonCompletionContributor extends CompletionContributor {

  public MuleArtifactJsonCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement(),
        new ClassBasedCompletionContributor((file) -> file.getName().equals("mule-artifact.json"), (file) -> {
          Optional<ProjectType> projectTypeOfFile = MuleModuleUtils.getProjectTypeOfFile(file);
          return projectTypeOfFile.map((pt) -> (Class<AbstractMuleArtifactModel>) pt.getArtifactJsonDescriptor()).orElse(AbstractMuleArtifactModel.class);
        }));
  }
}
