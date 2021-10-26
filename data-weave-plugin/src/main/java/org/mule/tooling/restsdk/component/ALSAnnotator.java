package org.mule.tooling.restsdk.component;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.mulesoft.lsp.feature.common.Range;
import org.mulesoft.lsp.feature.diagnostic.Diagnostic;
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity;

import java.util.List;

public class ALSAnnotator extends ExternalAnnotator<PsiFile, List<Diagnostic>> {

  @Override
  public @Nullable PsiFile collectInformation(@NotNull PsiFile file) {
    if (file.getFileType() == YAMLFileType.YML) {
      if (ALSLanguageService.getInstance(file.getProject()).isSupportedFile(file)) {
        return file;
      }
    }
    return null;
  }

  @Override
  public @Nullable List<Diagnostic> doAnnotate(PsiFile collectedInfo) {
    ALSLanguageService instance = ALSLanguageService.getInstance(collectedInfo.getProject());
    return instance.diagnosticsOf(collectedInfo);
  }

  @Override
  public void apply(@NotNull PsiFile file, List<Diagnostic> messages, @NotNull AnnotationHolder holder) {
    for (Diagnostic validationMessage : messages) {
      final Range location = validationMessage.range();
      int startIndex = StringUtil.lineColToOffset(file.getText(), location.start().line(), location.start().character());
      int endIndex = StringUtil.lineColToOffset(file.getText(), location.end().line(), location.end().character());
      HighlightSeverity severity = HighlightSeverity.ERROR;
      if (validationMessage.severity().isDefined()) {
        if (validationMessage.severity().get().id() == DiagnosticSeverity.Warning().id()) {
          severity = HighlightSeverity.WARNING;
        } else if (validationMessage.severity().get().id() == DiagnosticSeverity.Information().id()) {
          severity = HighlightSeverity.INFORMATION;
        } else if (validationMessage.severity().get().id() == DiagnosticSeverity.Hint().id()) {
          severity = HighlightSeverity.INFORMATION;
        }
      }

      if (startIndex < 0 || startIndex > file.getText().length()) {
        startIndex = 0;
      }

      if (endIndex < 0 || endIndex > file.getText().length()) {
        endIndex = 0;
      }
      holder.newAnnotation(severity, validationMessage.message())
              .range(new TextRange(startIndex, endIndex)).create();
    }
  }
}
