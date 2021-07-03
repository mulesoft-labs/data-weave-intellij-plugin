package org.mule.tooling.lang.dw.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.service.WeaveToolingService;

import java.util.List;

public class WeaveExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {


    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        Project project = completionParameters.getPosition().getProject();
        List<LookupElement> completion = WeaveToolingService.getInstance(project).completion(completionParameters);
        completionResultSet.addAllElements(completion);
    }
}
