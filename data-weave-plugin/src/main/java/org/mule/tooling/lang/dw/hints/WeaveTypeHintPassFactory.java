package org.mule.tooling.lang.dw.hints;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.codeInsight.hints.ModificationStampHolder;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;

public class WeaveTypeHintPassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory {

    public static ModificationStampHolder stampHolder = new ModificationStampHolder(Key.create("LAST_TYPE_PASS_MODIFICATION_TIMESTAMP"));


    protected WeaveTypeHintPassFactory(Project project, TextEditorHighlightingPassRegistrar registrar) {
        super(project);
        registrar.registerTextEditorHighlightingPass(this, null, null, false, -1);
    }

    @Nullable
    @Override
    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (file.getFileType() == WeaveFileType.getInstance()) {
            return new WeaveElementProcessingTypeHintPass(file, editor, stampHolder);
        }
        return null;
    }
}
