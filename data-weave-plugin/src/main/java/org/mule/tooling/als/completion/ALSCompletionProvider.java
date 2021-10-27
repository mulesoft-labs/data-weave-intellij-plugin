package org.mule.tooling.als.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.als.component.ALSLanguageService;

import java.util.List;

public class ALSCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
    final Project project = completionParameters.getOriginalFile().getProject();
    if (ALSLanguageService.getInstance(project).isSupportedFile(completionParameters.getOriginalFile())) {
      final PsiElement position = completionParameters.getPosition();
      final ALSLanguageService instance = ALSLanguageService.getInstance(project);
      final List<LookupElement> completion = instance.completion(position);
      result.addAllElements(completion);
    }
  }
}
