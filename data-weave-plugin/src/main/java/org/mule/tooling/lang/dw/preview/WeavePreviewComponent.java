package org.mule.tooling.lang.dw.preview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.ui.MessagePanel;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WeavePreviewComponent {

    public static final String INTEGRATION_TEST_FOLDER_NAME = "dwit";
    public static final String INPUT_FOLDER_NAME = "inputs";

    private List<Editor> editors = new ArrayList<>();
    private JBTabsPaneImpl inputTabs;
    private Project myProject;
    private ComboBox<VirtualFile> scenariosComboBox;
    private BorderLayoutPanel outputEditorPanel;
    private BorderLayoutPanel previewPanel;
    private final Document outputDocument;
    private Editor outputEditor;
    private String outputEditorMimeType;
    private PsiFile currentFile;


    public WeavePreviewComponent() {
        outputDocument = EditorFactory.getInstance().createDocument("");
    }

    public JComponent createComponent(Project project) {
        myProject = project;
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                if (currentFile == null) {
                    System.out.println("Ignoring change ;)");
                    return;
                }
                runPreview();
                //We know the change came from this file now
            }
        });
        return createPreviewPanel();
    }

    public void runPreview() {
        VirtualFile selectedItem = (VirtualFile) scenariosComboBox.getSelectedItem();
        if (selectedItem == null)
            return;

        final Document document = PsiDocumentManager.getInstance(myProject).getDocument(currentFile);
        final WeaveDocument weaveDocument = (WeaveDocument) currentFile.getChildren()[0];
        final WeaveAgentComponent agentComponent = WeaveAgentComponent.getInstance(myProject);
        final String inputsPath = selectedItem.findChild(INPUT_FOLDER_NAME).getPath();
        agentComponent.runPreview(inputsPath, document.getText(), weaveDocument.getQualifiedName(), currentFile.getVirtualFile().getUrl(), 10000L, new RunPreviewCallback() {

            @Override
            public void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result) {
                onPreviewResult(result);
            }

            @Override
            public void onPreviewFailed(PreviewExecutedFailedEvent message) {
                disposeEditorIfExists();
                Component component = outputEditorPanel.getComponent(0);
                if (component instanceof PreviewErrorPanel) {
                    ((PreviewErrorPanel) component).updateMessage(message.message());
                } else {
                    PreviewErrorPanel errorPanel = new PreviewErrorPanel(message.message());
                    changeMainPanel(errorPanel);
                }
            }


        });
    }

    public void changeMainPanel(JComponent errorPanel2) {
        outputEditorPanel.removeAll();
        outputEditorPanel.addToCenter(errorPanel2);
    }

    private ComboBoxModel<VirtualFile> createModel(VirtualFile[] scenarios) {
        return new DefaultComboBoxModel<>(scenarios);
    }

    @Nullable
    private VirtualFile[] findScenarios(PsiFile psiFile, VirtualFile integrationTestFolder) {
        WeaveDocument weaveDocument = getWeaveDocument(psiFile);
        if (weaveDocument != null) {
            boolean mappingDocument = weaveDocument.isMappingDocument();
            if (mappingDocument) {
                String qualifiedName = weaveDocument.getQualifiedName();
                if (qualifiedName != null) {
                    VirtualFile testDirectory = integrationTestFolder.findChild(qualifiedName);
                    if (testDirectory != null) {
                        return testDirectory.getChildren();
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private WeaveDocument getWeaveDocument(PsiFile psiFile) {
        PsiElement[] children = psiFile.getChildren();
        if (children.length > 0) {
            PsiElement child = children[0];
            if (child instanceof WeaveDocument) {
                return (WeaveDocument) child;
            }
        }
        return null;
    }

    @Nullable
    private VirtualFile findIntegrationTestFolder(VirtualFile[] sourceRoots) {
        for (VirtualFile sourceRoot : sourceRoots) {
            if (sourceRoot.isDirectory() && sourceRoot.getName().endsWith(INTEGRATION_TEST_FOLDER_NAME)) {
                return sourceRoot;
            }
        }
        return null;
    }

    private JComponent createPreviewPanel() {
        previewPanel = new BorderLayoutPanel();
        final JBSplitter splitter = new JBSplitter(false);
        inputTabs = new JBTabsPaneImpl(myProject, SwingConstants.TOP, myProject);
        outputEditorPanel = new BorderLayoutPanel();
        outputEditorPanel.addToCenter(new MessagePanel("No preview result to show yet."));
        splitter.setProportion(0.5f);
        splitter.setFirstComponent(inputTabs.getComponent());
        splitter.setSecondComponent(outputEditorPanel);

        final JPanel chooserPanel = createScenarioSelectorPanel();
        previewPanel.addToTop(chooserPanel);
//        previewPanel.addToCenter(inputTabs.getComponent());
        previewPanel.addToCenter(splitter);
        return previewPanel;
    }


    public void setFile(@Nullable PsiFile psiFile) {
        this.currentFile = psiFile;
        if (psiFile != null && psiFile.getFileType() == WeaveFileType.getInstance()) {
            Module moduleForFile = ModuleUtil.findModuleForFile(psiFile.getVirtualFile(), psiFile.getProject());
            if (moduleForFile != null) {
                VirtualFile[] sourceRoots = ModuleRootManager.getInstance(moduleForFile).getSourceRoots(true);
                VirtualFile integrationTestFolder = findIntegrationTestFolder(sourceRoots);
                if (integrationTestFolder != null) {
                    VirtualFile[] scenarios = findScenarios(psiFile, integrationTestFolder);
                    if (scenarios != null) {
                        scenariosComboBox.setModel(createModel(scenarios));
                        //Load first scenario
                        if (scenarios.length > 0) {
                            loadScenario(scenarios[0]);
                        }
                    }
                }
            }
        }
    }


    @NotNull
    private JPanel createScenarioSelectorPanel() {
        scenariosComboBox = new ComboBox<>();
        scenariosComboBox.setRenderer(new ScenarioNameRenderer());
        scenariosComboBox.addActionListener(evt -> {
            VirtualFile scenario = (VirtualFile) scenariosComboBox.getSelectedItem();
            if (scenario != null) {
                loadScenario(scenario);
            }
        });

        final JPanel chooserPanel = new JPanel(new GridBagLayout());
        final JLabel scopesLabel = new JLabel("Scenario:");
        scopesLabel.setDisplayedMnemonic('S');
        scopesLabel.setLabelFor(scenariosComboBox);
        final GridBagConstraints gc =
                new GridBagConstraints(
                        GridBagConstraints.RELATIVE,
                        0,
                        1,
                        1,
                        0,
                        0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        JBUI.insets(2),
                        0,
                        0);

        chooserPanel.add(scopesLabel, gc);
        chooserPanel.add(scenariosComboBox, gc);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        chooserPanel.add(Box.createHorizontalBox(), gc);

        return chooserPanel;
    }

    private void loadScenario(VirtualFile scenario) {
        VirtualFile inputs = scenario.findChild(INPUT_FOLDER_NAME);
        if (inputs != null && inputs.isDirectory()) {
            loadInputFiles(inputs);
        }
        runPreview();
    }

    public void onPreviewResult(PreviewExecutedSuccessfulEvent result) {
        final String extension = (result.extension().startsWith(".")) ? result.extension().substring(1) : result.extension();
        final String content = getContent(result);
        final String mimeType = result.mimeType();
        if (extension != null && (outputEditor == null || !mimeType.equals(outputEditorMimeType))) {
            disposeEditorIfExists();
            setDocumentContent(content);
            FileType fileTypeByExtension = FileTypeManager.getInstance().getFileTypeByExtension(extension);

            outputEditor = EditorFactory.getInstance().createEditor(outputDocument, myProject, fileTypeByExtension, true);
            changeMainPanel(outputEditor.getComponent());
            outputEditorMimeType = mimeType;
        } else if (extension != null) {
            setDocumentContent(content);
        } else {
            changeMainPanel(new MessagePanel("Unable to render the output for mimeType " + mimeType));
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

    public void disposeEditorIfExists() {
        if (outputEditor != null) {
            EditorFactory.getInstance().releaseEditor(outputEditor);
            outputEditor = null;
            outputEditorMimeType = null;
        }
    }

    private void loadInputFiles(VirtualFile inputs) {
        List<VirtualFile> children = VfsUtil.collectChildrenRecursively(inputs);
        for (VirtualFile input : children) {
            if (!input.isDirectory()) {
                PsiFile file = PsiManager.getInstance(myProject).findFile(input);
                if (file != null) {
                    Document document = file.getViewProvider().getDocument();
                    if (document != null) {
                        previewPanel.grabFocus();
                        inputTabs.getTabs().removeAllTabs();
                        Editor editor = EditorFactory.getInstance().createEditor(document, myProject, input, false);
                        editors.add(editor);
                        TabInfo tabInfo = new TabInfo(inputTabs.getComponent());
                        ItemPresentation presentation = file.getPresentation();
                        if (presentation != null) {
                            tabInfo.setText(presentation.getPresentableText());
                            tabInfo.setIcon(presentation.getIcon(false));
                        }
                        tabInfo.setComponent(editor.getComponent());
                        inputTabs.getTabs().addTab(tabInfo);
                    }
                }
            }
        }
    }


    public void close() {
        this.previewPanel.grabFocus();
        for (int i = 0; i < editors.size(); i++) {
            Editor editor = editors.get(i);
            if (!editor.isDisposed()) {
                EditorFactory.getInstance().releaseEditor(editor);
            }
        }
        this.editors.clear();
        this.scenariosComboBox.removeAllItems();
        this.currentFile = null;
    }


    private static class ScenarioNameRenderer extends ListCellRendererWrapper<VirtualFile> {
        @Override
        public void customize(JList list, VirtualFile value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                String scenario = StringUtil.capitalizeWords(value.getName(), "_", false, false);
                setText(scenario);
            }
        }
    }
}
