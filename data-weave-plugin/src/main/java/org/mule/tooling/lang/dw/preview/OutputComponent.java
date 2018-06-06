package org.mule.tooling.lang.dw.preview;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.ui.MessagePanel;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;

public class OutputComponent implements Disposable {

    private Project myProject;
    private final Document outputDocument;
    private Editor outputEditor;
    private String outputEditorFileExtension;
    private JPanel mainPanel;

    public OutputComponent() {
        outputDocument = EditorFactory.getInstance().createDocument("");
    }

    public JComponent createComponent(Project project) {
        myProject = project;
        return createOutputPanel();
    }

    private JComponent createOutputPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new MessagePanel("Waiting for preview execution to finish"));
        return mainPanel;
    }


    public void changeOutputPanel(JComponent editorComponent) {
        mainPanel.remove(0);
        mainPanel.add(editorComponent);
    }

    public void onPreviewResult(PreviewExecutedSuccessfulEvent result) {
        final String extension = (result.extension().startsWith(".")) ? result.extension().substring(1) : result.extension();
        final String content = getContent(result);
        if (extension != null && (outputEditor == null || !extension.equals(outputEditorFileExtension))) {
            disposeOutputEditorIfExists();
            setDocumentContent(content);
            FileType fileTypeByExtension = FileTypeManager.getInstance().getFileTypeByExtension(extension);
            outputEditor = EditorFactory.getInstance().createEditor(outputDocument, myProject, fileTypeByExtension, true);
            changeOutputPanel(outputEditor.getComponent());
            outputEditorFileExtension = extension;
        } else if (extension != null) {
            setDocumentContent(content);
        } else {
            changeOutputPanel(new MessagePanel("Unable to render the output for extension " + extension));
        }

    }

    @NotNull
    public String getContent(PreviewExecutedSuccessfulEvent result) {
        try {
            return new String(result.result(), result.mimeType());
        } catch (UnsupportedEncodingException e) {
            return new String(result.result());
        }
    }

    public void setDocumentContent(String content) {
        ApplicationManager.getApplication().runWriteAction(() -> outputDocument.setText(content));
    }

    public void disposeOutputEditorIfExists() {
        if (outputEditor != null) {
            EditorFactory.getInstance().releaseEditor(outputEditor);
            outputEditor = null;
            outputEditorFileExtension = null;
        }
    }

    @Override
    public void dispose() {
        disposeOutputEditorIfExists();
    }
}
