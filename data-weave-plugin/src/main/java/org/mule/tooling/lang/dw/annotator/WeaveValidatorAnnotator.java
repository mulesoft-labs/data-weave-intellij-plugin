package org.mule.tooling.lang.dw.annotator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.*;
import org.mule.tooling.lang.dw.util.AsyncCache;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.editor.QuickFix;
import org.mule.weave.v2.editor.ValidationMessage;
import org.mule.weave.v2.editor.ValidationMessages;
import org.mule.weave.v2.parser.ScopePhaseCategory$;
import org.mule.weave.v2.parser.location.Position;
import org.mule.weave.v2.parser.location.WeaveLocation;

import java.util.Optional;
import java.util.stream.Stream;

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
        final WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(file));
        if (weaveDocument == null) return null;
        final Project project = file.getProject();
        if (project.isDisposed()) return null;

        final WeaveRuntimeService scenariosManager = WeaveRuntimeService.getInstance(project);
        final WeaveToolingService toolingAPI = WeaveToolingService.getInstance(project);
        final ImplicitInput currentImplicitTypes;
        Optional<InputOutputTypesProvider> inputOutputTypesProvider = ReadAction.compute(() -> InputOutputTypesExtensionService.inputOutputTypesProvider(file));
        if (!inputOutputTypesProvider.isPresent()) {
            currentImplicitTypes = ReadAction.compute(() -> scenariosManager.getImplicitInputTypes(weaveDocument));
        } else {
            currentImplicitTypes = ReadAction.compute(() -> inputOutputTypesProvider.get().inputTypes(file));
        }
        final Boolean compute = ReadAction.compute(() -> WeavePsiUtils.getInputTypes(weaveDocument).isEmpty());
        //Also type check if
        final boolean shouldFilterVariableReferences = weaveDocument.isModuleDocument() || currentImplicitTypes != null || !compute;
        final ValidationMessages validationMessages = toolingAPI.typeCheck(file);
        if (shouldFilterVariableReferences) {
            return validationMessages;
        } else {
            final Stream<ValidationMessage> validationMessageStream = Stream.of(validationMessages.errorMessage());
            final ValidationMessage[] filteredMessage = validationMessageStream
                    .filter((message) -> !message.message().category().name().equals(ScopePhaseCategory$.MODULE$.name()))
                    .toArray(ValidationMessage[]::new);
            return new ValidationMessages(filteredMessage, validationMessages.warningMessage());
        }
    }

    @Override
    public void apply(@NotNull PsiFile file, ValidationMessages annotationResult, @NotNull AnnotationHolder holder) {
        final ValidationMessage[] errorMessage = annotationResult.errorMessage();
        apply(holder, errorMessage, HighlightSeverity.ERROR);
        final ValidationMessage[] validationMessages = annotationResult.warningMessage();
        apply(holder, validationMessages, HighlightSeverity.WARNING);
    }

    public void apply(@NotNull AnnotationHolder holder, ValidationMessage[] errorMessage, HighlightSeverity severity) {
        for (ValidationMessage validationMessage : errorMessage) {
            final WeaveLocation location = validationMessage.location();
            final int startIndex = getValidIndex(location.startPosition());
            final int endIndex = getValidIndex(location.endPosition());
            @NotNull AnnotationBuilder annotationBuilder = holder.newAnnotation(severity, validationMessage.message().message())
                    .range(new TextRange(startIndex, endIndex))
                    .tooltip(WeaveToolingService.toHtml(validationMessage.message().message()));
            final QuickFix[] quickFixes = validationMessage.quickFix();
            for (QuickFix quickFix : quickFixes) {
                annotationBuilder = annotationBuilder.withFix(new WeaveIntentionAction(quickFix));
            }
            annotationBuilder.create();

        }
    }

    private int getValidIndex(Position position) {
        int index = position.index();
        return Math.max(index, 0);
    }

    private static class WeaveIntentionAction implements IntentionAction {

        private QuickFix quickFix;

        public WeaveIntentionAction(QuickFix quickFix) {
            this.quickFix = quickFix;
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getText() {
            return quickFix.name();
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            return quickFix.description();
        }

        @Override
        public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
            return true;
        }

        @Override
        public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
            quickFix.quickFix().run(new IJWeaveTextDocument(editor, project));
        }

        @Override
        public boolean startInWriteAction() {
            return false;
        }

    }

}
