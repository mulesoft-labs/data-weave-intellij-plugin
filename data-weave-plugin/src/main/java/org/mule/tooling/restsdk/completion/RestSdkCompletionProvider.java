package org.mule.tooling.restsdk.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.LineColumn;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.restsdk.component.ALSLanguageService;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import java.util.List;

public class RestSdkCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiElement position = completionParameters.getPosition();
    final Project project = position.getProject();
    if (RestSdkHelper.isRestSdkDescriptorFile(completionParameters.getOriginalFile())) {
      final RestSdkCompletionService restSdkCompletionService = new RestSdkCompletionService();
      result.addAllElements(restSdkCompletionService.completions(completionParameters));
      final ALSLanguageService instance = ALSLanguageService.getInstance(project);
      final PsiFile originalFile = completionParameters.getOriginalFile();
      final LineColumn lineNumber = StringUtil.offsetToLineColumn(originalFile.getText(), position.getTextOffset());
      final List<LookupElement> completion = instance.completion(originalFile, lineNumber.line, lineNumber.column);
      result.addAllElements(completion);
    }
  }
}


