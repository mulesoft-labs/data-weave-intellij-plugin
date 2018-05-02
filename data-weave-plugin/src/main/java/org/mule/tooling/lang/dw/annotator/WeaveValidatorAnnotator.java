package org.mule.tooling.lang.dw.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.weave.v2.editor.ValidationMessage;
import org.mule.weave.v2.editor.ValidationMessages;
import org.mule.weave.v2.parser.location.WeaveLocation;

public class WeaveValidatorAnnotator extends ExternalAnnotator<PsiFile, ValidationMessages> {
    @Nullable
    @Override
    public PsiFile collectInformation(@NotNull PsiFile file) {
        if (file.getFileType() == WeaveFileType.getInstance()) {
            return file;
        } else {
            return null;
        }
    }


    @Nullable
    @Override
    public ValidationMessages doAnnotate(PsiFile file) {
        WeaveDocument weaveDocument = ApplicationManager.getApplication().runReadAction((Computable<WeaveDocument>) () -> WeavePsiUtils.getWeaveDocument(file));
        if (weaveDocument != null) {
            return ApplicationManager.getApplication().runReadAction((Computable<ValidationMessages>) () -> {
                DataWeaveScenariosManager scenariosManager = DataWeaveScenariosManager.getInstance(file.getProject());
                DWEditorToolingAPI toolingAPI = DWEditorToolingAPI.getInstance(file.getProject());
                if (weaveDocument.isModuleDocument() || scenariosManager.getCurrentImplicitTypes(WeavePsiUtils.getWeaveDocument(file)) != null) {
                    return toolingAPI.typeCheck(file);
                } else {
                    return toolingAPI.parseCheck(file);
                }
            });
        } else {
            return null;
        }

    }

    @Override
    public void apply(@NotNull PsiFile file, ValidationMessages annotationResult, @NotNull AnnotationHolder holder) {
        ValidationMessage[] errorMessage = annotationResult.errorMessage();
        apply(holder, errorMessage, HighlightSeverity.ERROR);
        ValidationMessage[] validationMessages = annotationResult.warningMessage();
        apply(holder, validationMessages, HighlightSeverity.WARNING);

    }

    public void apply(@NotNull AnnotationHolder holder, ValidationMessage[] errorMessage, HighlightSeverity severity) {
        for (ValidationMessage validationMessage : errorMessage) {
            WeaveLocation location = validationMessage.location();
            int startIndex = location.startPosition().index();
            int endIndex = location.endPosition().index();
            holder.createAnnotation(severity, new TextRange(startIndex, endIndex), validationMessage.message().message());
        }
    }

}
