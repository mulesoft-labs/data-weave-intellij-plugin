package org.mule.tooling.lang.dw.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;
import org.mule.tooling.lang.dw.util.AsyncCache;
import org.mule.weave.v2.editor.ValidationMessage;
import org.mule.weave.v2.editor.ValidationMessages;
import org.mule.weave.v2.parser.location.Position;
import org.mule.weave.v2.parser.location.WeaveLocation;

public class WeaveDocsAnnotator extends ExternalAnnotator<PsiFile, ValidationMessages> {
    private AsyncCache<PsiFile, ValidationMessages> cache;

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
        final WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(file));
        if (weaveDocument == null) return null;
        final Project project = file.getProject();
        if (project.isDisposed()) return null;
        final WeaveEditorToolingAPI toolingAPI = WeaveEditorToolingAPI.getInstance(project);
        if (cache == null) {
            cache = new AsyncCache<>(toolingAPI::weaveDocCheck);
            toolingAPI.addOnCloseListener(() -> {
                // throw away cache on project close.
                cache = null;
            });
        }
        return cache.resolve(file).orElse(null);

    }

    @Override
    public void apply(@NotNull PsiFile file, ValidationMessages annotationResult, @NotNull AnnotationHolder holder) {
        final ValidationMessage[] errorMessage = annotationResult.errorMessage();
        apply(holder, errorMessage, HighlightSeverity.WARNING);
        final ValidationMessage[] validationMessages = annotationResult.warningMessage();
        apply(holder, validationMessages, HighlightSeverity.WARNING);
    }

    public void apply(@NotNull AnnotationHolder holder, ValidationMessage[] errorMessage, HighlightSeverity severity) {
        for (ValidationMessage validationMessage : errorMessage) {
            final WeaveLocation location = validationMessage.location();
            final int startIndex = getValidIndex(location.startPosition());
            final int endIndex = getValidIndex(location.endPosition());
            holder.createAnnotation(severity, new TextRange(startIndex, endIndex), validationMessage.message().message(), WeaveEditorToolingAPI.toHtml(validationMessage.message().message()));
        }
    }

    private int getValidIndex(Position position) {
        int index = position.index();
        if (index < 0) {
            return 0;
        } else {
            return index;
        }
    }

}
