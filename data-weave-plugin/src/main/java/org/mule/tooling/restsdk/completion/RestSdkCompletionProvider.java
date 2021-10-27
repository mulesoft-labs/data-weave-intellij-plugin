package org.mule.tooling.restsdk.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

public class RestSdkCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiElement position = completionParameters.getPosition();
    final Project project = position.getProject();
    if (RestSdkHelper.isRestSdkDescriptorFile(completionParameters.getOriginalFile())) {
      final RestSdkCompletionService restSdkCompletionService = new RestSdkCompletionService();
      result.addAllElements(restSdkCompletionService.completions(completionParameters));
    }
  }
}


