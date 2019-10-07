package org.mule.tooling.lang.dw.preview;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeContextManager;
import org.mule.tooling.lang.dw.ui.MessagePanel;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mule.tooling.lang.dw.WeaveConstants.DEFAULT_SCENARIO_NAME;

public class OutputComponent implements Disposable {

    public static final String EDITOR_PANEL = "editorPanel";
    public static final String DIFF_PANEL = "diffPanel";
    public static final String MESSAGE_PANEL = "messagePanel";

    private Project myProject;
    private PreviewToolWindowFactory.NameChanger nameChanger;
    private PsiFile currentFile;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private final Document outputDocument;
    private Editor outputEditor;
    private String outputEditorFileExtension;
    private FileType outputType;
    private JPanel editorPanel;
    private JPanel messagePanel;
    private DiffRequestPanel diffPanel;
    private ShowDiffAction showDiffAction;
    private Content content;

    public OutputComponent() {
        outputDocument = EditorFactory.getInstance().createDocument("");
    }

    public JComponent createComponent(Project project, PreviewToolWindowFactory.NameChanger nameChanger) {
        this.myProject = project;
        this.nameChanger = nameChanger;
        this.showDiffAction = new ShowDiffAction();
        return createOutputPanel();
    }

    private JComponent createOutputPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new MessagePanel("Waiting for preview execution to finish"));
        mainPanel.add(messagePanel, MESSAGE_PANEL);

        editorPanel = new JPanel(new BorderLayout());
        mainPanel.add(editorPanel, EDITOR_PANEL);

        diffPanel = DiffManager.getInstance().createRequestPanel(myProject, this, null);
        mainPanel.add(diffPanel.getComponent(), DIFF_PANEL);
        show(MESSAGE_PANEL);
        return mainPanel;
    }

    public DefaultActionGroup createActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new SaveOutputAction());
        group.add(showDiffAction);
        return group;
    }

    public void add(JComponent component, String name) {
        mainPanel.add(component, name);
    }

    public void changePanel(JComponent newComponent, JComponent parent, String name) {
        if (parent.getComponentCount() > 0) {
            parent.remove(0);
        }
        parent.add(newComponent);
        cardLayout.show(mainPanel, name);
    }

    public void show(String name) {
        cardLayout.show(mainPanel, name);
    }

    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public void setEditorContainer(Content content) {

        this.content = content;
    }

    public void onPreviewResult(PreviewExecutedSuccessfulEvent result, VirtualFile expectedOutput, long duration) {
        this.content.setDisplayName("Output \u2713 - Took: " + duration + " ms - At:" + getCurrentTime() + "");
        final String extension = (result.extension().startsWith(".")) ? result.extension().substring(1) : result.extension();
        final String content = getContent(result);
        if (expectedOutput != null) {
            diffPanel.setRequest(createDiffRequest(content, expectedOutput));
        }
        if (extension != null && (outputEditor == null || !extension.equals(outputEditorFileExtension))) {
            disposeOutputEditorIfExists();
            setDocumentContent(content);
            outputType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
            outputEditor = EditorFactory.getInstance().createEditor(outputDocument, myProject, outputType, true);
            outputEditorFileExtension = extension;
            if (!showDiffAction.isSelected) {
                changePanel(outputEditor.getComponent(), editorPanel, EDITOR_PANEL);
            }
        } else if (extension != null) {
            setDocumentContent(content);
        } else {
            changePanel(new MessagePanel("Unable to render the output for extension " + extension), messagePanel, MESSAGE_PANEL);
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

    public FileType getOutputType() {
        return outputType;
    }

    @Override
    public void dispose() {
        disposeOutputEditorIfExists();
        if (diffPanel != null) {
            Disposer.dispose(diffPanel);
            diffPanel = null;
        }
    }

    public void onPreviewResultFailed() {
        this.content.setDisplayName("Output \u274C - At: " + getCurrentTime() + "");
    }

    private class SaveOutputAction extends AnAction {
        public SaveOutputAction() {
            super(null, "Save expected output", AllIcons.Actions.Menu_saveall);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            super.update(e);
            e.getPresentation().setEnabled(outputEditor != null);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Scenario scenario = getOrCreateScenario();
            String ext = outputType.getDefaultExtension();
            scenario.addOutput("out." + ext, outputDocument.getText(), myProject);
        }

        private Scenario getOrCreateScenario() {
            WeaveDocument document = WeavePsiUtils.getWeaveDocument(currentFile);
            WeaveRuntimeContextManager manager = WeaveRuntimeContextManager.getInstance(myProject);
            Scenario currentScenario = manager.getCurrentScenarioFor(document);
            if (currentScenario == null || !currentScenario.isValid()) {
                //create scenario
                currentScenario = manager.createScenario(currentFile, DEFAULT_SCENARIO_NAME);
                if (currentScenario != null) {
                    nameChanger.changeName("Scenario: " + currentScenario.getPresentableText());
                }
            }
            return currentScenario;
        }
    }

    private class ShowDiffAction extends ToggleAction {
        private boolean isSelected;

        public ShowDiffAction() {
            super(null, "Show diff", AllIcons.Actions.Diff);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            super.update(e);
            e.getPresentation().setEnabled(outputEditor != null);
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            return isSelected;
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            isSelected = state;
            if (isSelected) {
                show(DIFF_PANEL);
            } else {
                show(EDITOR_PANEL);
            }
        }
    }

    @NotNull
    private DiffRequest createDiffRequest(String actualText, VirtualFile expectedOutput) {
        DiffContent expectedContent = DiffContentFactory.getInstance().create(myProject, expectedOutput);
        FileType outputType = getOutputType();
        DiffContent actualContent = DiffContentFactory.getInstance().create(myProject, actualText, outputType);
        return new SimpleDiffRequest("Diff", expectedContent, actualContent, "Expected", "Actual");
    }

    public void setCurrentFile(PsiFile currentFile) {
        this.currentFile = currentFile;
    }
}
