package org.mule.tooling.lang.dw.preview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.ui.MessagePanel;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.io.UnsupportedEncodingException;

public class OutputComponent implements Disposable {

    private Project myProject;
    private final Document outputDocument;
    private Editor outputEditor;
    private String outputEditorFileExtension;
//    private boolean runOnChange = true;

    private JBTabsPaneImpl outputTabs;
    private PreviewLogsViewer previewLogsViewer;


    public OutputComponent() {
        outputDocument = EditorFactory.getInstance().createDocument("");
    }

    public JComponent createComponent(Project project) {
        myProject = project;
        return createOutputPanel();
    }

    private JComponent createOutputPanel() {
        BorderLayoutPanel outputPanel = new BorderLayoutPanel();
        outputTabs = new JBTabsPaneImpl(myProject, SwingConstants.TOP, myProject);
        final TabInfo outputTabInfo = new TabInfo(new MessagePanel("Waiting for preview execution to finish"));
        outputTabInfo.setText("Output");
        outputTabInfo.setIcon(AllIcons.General.Information);
        outputTabs.getTabs().addTab(outputTabInfo);
        previewLogsViewer = new PreviewLogsViewer(myProject);
        TabInfo logTabInfo = new TabInfo(previewLogsViewer);
        logTabInfo.setText("Logs/Errors");
        logTabInfo.setIcon(AllIcons.Debugger.Console_log);

        outputTabs.getTabs().addTab(logTabInfo);
        outputPanel.add(outputTabs.getComponent());
        return outputPanel;
    }

    public void runPreview(Scenario selectedItem, PsiFile currentFile) {
        ProgressManager.getInstance().run(new Task.Backgroundable(myProject, "Run Preview of DataWEave") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    if (selectedItem == null)
                        return;

                    final Document document = PsiDocumentManager.getInstance(myProject).getDocument(currentFile);
                    PsiElement psiElement = currentFile.getChildren()[0];
                    if (!(psiElement instanceof WeaveDocument)) {
                        // When there are errors it may not be a WeaveDocument
                        return;
                    }
                    final WeaveDocument weaveDocument = (WeaveDocument) psiElement;
                    final WeaveAgentComponent agentComponent = WeaveAgentComponent.getInstance(myProject);
                    final String inputsPath = selectedItem.getInputs().getPath();
                    final Module module = ModuleUtil.findModuleForFile(currentFile.getVirtualFile(), myProject);
                    agentComponent.runPreview(inputsPath, document.getText(), weaveDocument.getQualifiedName(), currentFile.getVirtualFile().getUrl(), 10000L, module, new RunPreviewCallback() {

                        @Override
                        public void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result) {
                            onPreviewResult(result);
                            previewLogsViewer.clear();
                            previewLogsViewer.logInfo(ScalaUtils.toList(result.messages()));
                        }

                        @Override
                        public void onPreviewFailed(PreviewExecutedFailedEvent message) {
                            previewLogsViewer.clear();
                            previewLogsViewer.logInfo(ScalaUtils.toList(message.messages()));
                            previewLogsViewer.logError(message.message());
                        }
                    });
                });

            }
        });


    }

    public void changeOutputPanel(JComponent outputPanel, String title, Icon icon) {
        outputTabs.removeTabAt(0);
        TabInfo outputTabInfo = new TabInfo(outputPanel);
        outputTabInfo.setText(title);
        outputTabInfo.setIcon(icon);
        outputTabs.getTabs().addTab(outputTabInfo, 0);
    }

    public void onPreviewResult(PreviewExecutedSuccessfulEvent result) {
        final String extension = (result.extension().startsWith(".")) ? result.extension().substring(1) : result.extension();
        final String content = getContent(result);
        if (extension != null && (outputEditor == null || !extension.equals(outputEditorFileExtension))) {
            disposeOutputEditorIfExists();
            setDocumentContent(content);
            FileType fileTypeByExtension = FileTypeManager.getInstance().getFileTypeByExtension(extension);
            outputEditor = EditorFactory.getInstance().createEditor(outputDocument, myProject, fileTypeByExtension, true);
            changeOutputPanel(outputEditor.getComponent(), "Output", fileTypeByExtension.getIcon());
            outputEditorFileExtension = extension;
        } else if (extension != null) {
            setDocumentContent(content);
        } else {
            changeOutputPanel(new MessagePanel("Unable to render the output for extension " + extension), "Error", AllIcons.General.Error);
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
        previewLogsViewer.dispose();
    }

//    public boolean runAvailable() {
//        if (currentFile != null && scenariosComboBox != null) {
//            Scenario selectedItem = (Scenario) scenariosComboBox.getSelectedItem();
//            return selectedItem != null;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean runOnChange() {
//        return runOnChange;
//    }
//
//    public void runOnChange(boolean state) {
//        this.runOnChange = state;
//    }

}
