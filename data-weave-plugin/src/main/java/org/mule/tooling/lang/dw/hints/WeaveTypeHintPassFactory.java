package org.mule.tooling.lang.dw.hints;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.codeInsight.hints.ModificationStampHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.settings.DataWeaveSettingsState;

public class WeaveTypeHintPassFactory implements TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {

    public static ModificationStampHolder stampHolder = new ModificationStampHolder(Key.create("LAST_TYPE_PASS_MODIFICATION_TIMESTAMP"));

    public void registerHighlightingPassFactory(TextEditorHighlightingPassRegistrar registrar, @NotNull Project project) {
        registrar.registerTextEditorHighlightingPass(this, null, null, false, -1);
    }

    @Nullable
    @Override
    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (DataWeaveSettingsState.getInstance().getShowParametersName() && file.getFileType() == WeaveFileType.getInstance() && !DataWeaveSettingsState.getInstance().isBigFileForSemanticAnalysis(file)) {
            return new WeaveElementProcessingTypeHintPass(file, editor, stampHolder);
        }
        return null;
    }
}
