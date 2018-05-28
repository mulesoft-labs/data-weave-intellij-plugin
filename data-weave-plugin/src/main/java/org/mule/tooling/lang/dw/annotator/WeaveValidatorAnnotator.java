package org.mule.tooling.lang.dw.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
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
import org.mule.tooling.lang.dw.util.AsyncCache;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.editor.ValidationMessage;
import org.mule.weave.v2.editor.ValidationMessages;
import org.mule.weave.v2.parser.location.Position;
import org.mule.weave.v2.parser.location.WeaveLocation;

public class WeaveValidatorAnnotator extends ExternalAnnotator<PsiFile, ValidationMessages> {
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
        WeaveDocument weaveDocument = ApplicationManager.getApplication().runReadAction((Computable<WeaveDocument>) () -> WeavePsiUtils.getWeaveDocument(file));
        if (weaveDocument == null) {
            return null;
        }
        DataWeaveScenariosManager scenariosManager = DataWeaveScenariosManager.getInstance(file.getProject());
        DWEditorToolingAPI toolingAPI = DWEditorToolingAPI.getInstance(file.getProject());
        ImplicitInput currentImplicitTypes = ReadAction.compute(() -> scenariosManager.getCurrentImplicitTypes(weaveDocument));
        if (weaveDocument.isModuleDocument() || currentImplicitTypes != null) {
            return toolingAPI.typeCheck(file);
        } else {
            if (cache == null) {
                cache = new AsyncCache<>(toolingAPI::parseCheck);
            }
            return cache.resolve(file).orElse(null);
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
            int startIndex = getValidIndex(location.startPosition());
            int endIndex = getValidIndex(location.endPosition());
            holder.createAnnotation(severity, new TextRange(startIndex, endIndex), validationMessage.message().message());
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
