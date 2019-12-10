package org.mule.tooling.lang.dw.paste;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;

import java.util.Arrays;

public class StringLiteralPaste implements CopyPastePreProcessor {
    @Nullable
    @Override
    public String preprocessOnCopy(PsiFile file, int[] startOffsets, int[] endOffsets, String text) {
        boolean literal = true;
        for (int i = 0; i < startOffsets.length && i < endOffsets.length; i++) {
            int startOffset = startOffsets[i];
            int endOffset = endOffsets[i];
            final PsiElement e = file.findElementAt(startOffset);
            if (e != null) {
                literal = literal && e.getLanguage().isKindOf(WeaveLanguage.getInstance()) && e.getNode() != null &&
                        isStringType(e.getNode().getElementType()) &&
                        startOffset > e.getTextRange().getStartOffset() && endOffset < e.getTextRange().getEndOffset();
            }
        }
        if (literal) {
            return StringUtil.unescapeStringCharacters(text);
        } else {
            return null;
        }
    }

    @NotNull
    @Override
    public String preprocessOnPaste(Project project, PsiFile file, Editor editor, String text, RawText rawText) {
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        final int offset = editor.getSelectionModel().getSelectionStart();
        final PsiElement e = file.findElementAt(offset);
        if (e != null && e.getLanguage().isKindOf(WeaveLanguage.getInstance()) && offset > e.getTextOffset()) {
            final IElementType elementType = (e.getNode() == null) ? null : e.getNode().getElementType();
            if (isStringType(elementType) && rawText != null && rawText.rawText != null) {
                return rawText.rawText;
            } else if (isStringType(elementType)) {
                return Arrays.stream(LineTokenizer.tokenize(text.toCharArray(), false, true))
                        .map(line -> StringUtil.escapeStringCharacters(line))
                        .reduce((a, b) -> a + "\\n" + b).orElse(text);
            } else {
                return text;
            }
        } else {
            return text;
        }
    }

    private boolean isStringType(IElementType elementType) {
        return elementType == WeaveTypes.DOUBLE_QUOTED_STRING || elementType == WeaveTypes.SINGLE_QUOTED_STRING || elementType == WeaveTypes.BACKTIKED_QUOTED_STRING;
    }
}
