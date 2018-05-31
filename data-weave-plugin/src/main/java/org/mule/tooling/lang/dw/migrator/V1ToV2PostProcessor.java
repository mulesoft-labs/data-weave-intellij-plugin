package org.mule.tooling.lang.dw.migrator;

import com.intellij.codeInsight.editorActions.CopyPastePostProcessor;
import com.intellij.codeInsight.editorActions.TextBlockTransferableData;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.weave.v2.V2LangMigrant;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class V1ToV2PostProcessor extends CopyPastePostProcessor<TextBlockTransferableData> {
    @NotNull
    @Override
    public List<TextBlockTransferableData> collectTransferableData(PsiFile file, Editor editor, int[] startOffsets, int[] endOffsets) {

        return Collections.emptyList();
    }

    private boolean isV1Text(String textBetweenOffsets) {
        return textBetweenOffsets.trim().startsWith("%dw 1.0")
                || textBetweenOffsets.contains("%input ")
                || textBetweenOffsets.contains("%output ")
                || textBetweenOffsets.contains("%function ")
                || textBetweenOffsets.contains("%var ");
    }

    @Override
    public void processTransferableData(Project project, Editor editor, RangeMarker bounds, int caretOffset, Ref<Boolean> indented, List<TextBlockTransferableData> values) {
        if (values.size() == 1 && values.get(0) instanceof ConvertedCode) {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if(psiFile != null && psiFile.getFileType() == WeaveFileType.getInstance()) {
                YesNoDialog migrate_to_v2 = new YesNoDialog("Migrate To V2", "Do you want to migrate the DW 1.0 to DW 2.0", null, project);
                if (migrate_to_v2.showAndGet()) {
                    final String v2 = V2LangMigrant.migrateToV2(((ConvertedCode) values.get(0)).data);
                    WriteAction.run(() -> {
                        editor.getDocument().replaceString(bounds.getStartOffset(), bounds.getEndOffset(), v2);
                    });
                }
            }
        }
    }

    @NotNull
    @Override
    public List<TextBlockTransferableData> extractTransferableData(Transferable content) {
        if (content.isDataFlavorSupported(ConvertedCode.FLAVOR)) {
            try {
                return Collections.singletonList((TextBlockTransferableData) content.getTransferData(ConvertedCode.FLAVOR));
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = content.getTransferData(DataFlavor.stringFlavor).toString();
                if (isV1Text(text)) {
                    return Collections.singletonList(new ConvertedCode(text));
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return super.extractTransferableData(content);
    }

    String getTextBetweenOffsets(PsiFile file, int[] startOffsets, int[] endOffsets) {
        StringBuilder builder = new java.lang.StringBuilder();
        ArrayList<Integer> ranges = new ArrayList<>();
        for (int startOffset : startOffsets) {
            ranges.add(startOffset);
        }

        for (int endOffset : endOffsets) {
            ranges.add(endOffset);
        }

        Collections.sort(ranges);
        for (int i = 0; i < ranges.size(); i = i + 2) {
            Integer startOffset = ranges.get(i);
            if (i + 1 < ranges.size()) {
                Integer endOffset = ranges.get(i + 1);
                builder.append(charSequence(file).subSequence(startOffset, endOffset));
            }
        }
        return builder.toString();
    }

    public static CharSequence charSequence(PsiFile file) {
        if (file.isValid()) {
            return file.getViewProvider().getContents();
        }
        return file.getText();
    }


    public static class ConvertedCode implements TextBlockTransferableData {
        public static DataFlavor FLAVOR = new DataFlavor(ConvertedCode.class, "DWV1ToV2Migrator");
        private String data;

        public ConvertedCode(String data) {
            this.data = data;
        }

        @Override
        public DataFlavor getFlavor() {
            return FLAVOR;
        }

        @Override
        public int getOffsetCount() {
            return 1;
        }

        @Override
        public int getOffsets(int[] offsets, int index) {
            return index;
        }

        @Override
        public int setOffsets(int[] offsets, int index) {
            return index;
        }
    }
}


//class ConvertedCode(val data: String, val associations: Array[Association], val showDialog: Boolean = false) extends TextBlockTransferableData {
//        def setOffsets(offsets: Array[Int], _index: Int): Int = {
//        var index = _index
//        for (association <- associations) {
//        association.range = new TextRange(offsets(index), offsets(index + 1))
//        index += 2
//        }
//        index
//        }
//
//        def getOffsets(offsets: Array[Int], _index: Int): Int = {
//        var index = _index
//        for (association <- associations) {
//        offsets(index) = association.range.getStartOffset
//        index += 1
//        offsets(index) = association.range.getEndOffset
//        index += 1
//        }
//        index
//        }
//
//        def getOffsetCount: Int = associations.length * 2
//
//        def getFlavor: DataFlavor = ConvertedCode.Flavor
//        }
//
//        def replaceByConvertedCode(editor: Editor, bounds: RangeMarker, text: String): Unit = {
//        val document = editor.getDocument
//
//        def hasQuoteAt(offset: Int) = {
//        val chars = document.getCharsSequence
//        offset >= 0 && offset <= chars.length() && chars.charAt(offset) == '\"'
//        }
//
//        val start = bounds.getStartOffset
//        val end = bounds.getEndOffset
//        val isInsideStringLiteral = hasQuoteAt(start - 1) && hasQuoteAt(end)
//        if (isInsideStringLiteral && text.startsWith("\"") && text.endsWith("\""))
//        document.replaceString(start - 1, end + 1, text)
//        else document.replaceString(start, end, text)
//        }
//
//        object ConvertedCode {
//        lazy val Flavor: DataFlavor = new DataFlavor(classOf[ConvertedCode], "JavaToScalaConvertedCode")
//        }
//
//        def shownDialog(msg: String, project: Project): ScalaPasteFromJavaDialog = {
//        val dialog = new ScalaPasteFromJavaDialog(project, msg)
//        dialog.show()
//        dialog
//        }
//        }