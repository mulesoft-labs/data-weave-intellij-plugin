package org.mule.tooling.lang.dw.preview;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.Alarm;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.ui.MessagePanel;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WeavePreviewComponent implements Disposable {

    private List<Editor> editors = new ArrayList<>();
    private JBTabsPaneImpl inputTabs;
    private Project myProject;
    private ComboBox<Scenario> scenariosComboBox;
    private BorderLayoutPanel previewPanel;
    private final Document outputDocument;
    private Editor outputEditor;
    private String outputEditorFileExtension;
    private PsiFile currentFile;
    private boolean runOnChange = true;

    private Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private JBTabsPaneImpl outputTabs;
    private PreviewLogsViewer previewLogsViewer;

    public WeavePreviewComponent() {
        outputDocument = EditorFactory.getInstance().createDocument("");
    }

    public JComponent createComponent(Project project) {
        myProject = project;
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                if (currentFile == null || !runOnChange) {
                    return;
                }

                //We call all the request and add a new one if in 200 milliseconds no change was introduce then trigger preview
                myDocumentAlarm.cancelAllRequests();
                myDocumentAlarm.addRequest(() -> {
                    if (myDocumentAlarm.isDisposed())
                        return;
                    runPreview();
                }, WeaveConstants.MODIFICATIONS_DELAY);

                //We know the change came from this file now
            }
        }, this);
        return createPreviewPanel();
    }

    public PsiFile getCurrentFile() {
        return currentFile;
    }

    public void runPreview() {

        ApplicationManager.getApplication().runReadAction(() -> {
            Scenario selectedItem = (Scenario) scenariosComboBox.getSelectedItem();
            if (selectedItem == null)
                return;

            final Document document = PsiDocumentManager.getInstance(myProject).getDocument(currentFile);
            final WeaveDocument weaveDocument = (WeaveDocument) currentFile.getChildren()[0];
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

    public void changeOutputPanel(JComponent outputPanel, String title, Icon icon) {
        outputTabs.removeTabAt(0);
        TabInfo outputTabInfo = new TabInfo(outputPanel);
        outputTabInfo.setText(title);
        outputTabInfo.setIcon(icon);
        outputTabs.getTabs().addTab(outputTabInfo, 0);
    }

    private ComboBoxModel<Scenario> createModel(List<Scenario> scenarios) {
        return new DefaultComboBoxModel<>(scenarios.toArray(new Scenario[0]));
    }

    private JComponent createPreviewPanel() {
        previewPanel = new BorderLayoutPanel();
        final JBSplitter splitter = new JBSplitter(false);
        inputTabs = new JBTabsPaneImpl(myProject, SwingConstants.TOP, myProject);
        outputTabs = new JBTabsPaneImpl(myProject, SwingConstants.TOP, myProject);
        splitter.setProportion(0.5f);
        splitter.setFirstComponent(inputTabs.getComponent());
        splitter.setSecondComponent(outputTabs.getComponent());

        final TabInfo outputTabInfo = new TabInfo(new MessagePanel("Waiting for preview execution to finish"));
        outputTabInfo.setText("Output");
        outputTabInfo.setIcon(AllIcons.General.Information);
        outputTabs.getTabs().addTab(outputTabInfo);
        previewLogsViewer = new PreviewLogsViewer(myProject);
        TabInfo logTabInfo = new TabInfo(previewLogsViewer);
        logTabInfo.setText("Console");
        logTabInfo.setIcon(AllIcons.Debugger.Console_log);

        outputTabs.getTabs().addTab(logTabInfo);

        final JPanel chooserPanel = createScenarioSelectorPanel();
        previewPanel.addToTop(chooserPanel);
        previewPanel.addToCenter(splitter);
        return previewPanel;
    }


    public void open(@Nullable PsiFile psiFile) {
        if (currentFile == psiFile) {
            return;
        }
        if (psiFile != null && psiFile.getFileType() == WeaveFileType.getInstance()) {
            this.currentFile = psiFile;
            DataWeaveScenariosManager instance = DataWeaveScenariosManager.getInstance(myProject);
            WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            List<Scenario> scenarios = instance.getScenariosFor(weaveDocument);
            scenariosComboBox.setModel(createModel(scenarios));
            //Load first scenario
            if (!scenarios.isEmpty()) {
                Scenario currentScenarioFor = instance.getCurrentScenarioFor(weaveDocument);
                if (currentScenarioFor != null) {
                    loadScenario(currentScenarioFor);
                }
            }
        }
    }


    @NotNull
    private JPanel createScenarioSelectorPanel() {
        scenariosComboBox = new ComboBox<>();
        scenariosComboBox.setRenderer(new ScenarioNameRenderer());
        scenariosComboBox.addActionListener(evt -> {
            Scenario scenario = (Scenario) scenariosComboBox.getSelectedItem();
            if (scenario != null) {
                DataWeaveScenariosManager.getInstance(myProject).setCurrentScenario(getCurrentWeaveDocument(), scenario);
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

    private WeaveDocument getCurrentWeaveDocument() {
        return WeavePsiUtils.getWeaveDocument(currentFile);
    }

    private void loadScenario(Scenario scenario) {
        final VirtualFile inputs = scenario.getInputs();
        if (inputs != null && inputs.isDirectory()) {
            loadInputFiles(inputs);
        }
        runPreview();
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

    private void loadInputFiles(VirtualFile inputs) {
        closeAllInputs();
        List<VirtualFile> children = VfsUtil.collectChildrenRecursively(inputs);
        for (VirtualFile input : children) {
            if (!input.isDirectory()) {
                PsiFile file = PsiManager.getInstance(myProject).findFile(input);
                if (file != null) {
                    Document document = file.getViewProvider().getDocument();
                    if (document != null) {
                        Editor editor = EditorFactory.getInstance().createEditor(document, myProject, input, false);
                        editors.add(editor);
                        TabInfo tabInfo = new TabInfo(inputTabs.getComponent());
                        ItemPresentation presentation = file.getPresentation();
                        if (presentation != null) {
                            final String relativeLocation = VfsUtil.getRelativeLocation(input, inputs);
                            assert relativeLocation != null;
                            String expression = relativeLocation.replace('/', '.');
                            String extension = input.getExtension();
                            if (extension != null) {
                                //extension doesn't have the dot so we need to add a + 1
                                expression = expression.substring(0, expression.length() - (extension.length() + 1)) + " (" + StringUtil.capitalize(extension) + ")";
                            }
                            tabInfo.setText(expression);
                            tabInfo.setIcon(presentation.getIcon(false));
                        }
                        tabInfo.setComponent(editor.getComponent());
                        inputTabs.getTabs().addTab(tabInfo);
                        inputTabs.setSelectedIndex(0);
                    }
                }
            }
        }
    }

    private void closeAllInputs() {
        previewPanel.grabFocus();
        inputTabs.getTabs().removeAllTabs();
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
        disposeOutputEditorIfExists();
        this.currentFile = null;
    }

    @Override
    public void dispose() {
        close();
    }

    public boolean runAvailable() {
        if (currentFile != null && scenariosComboBox != null) {
            Scenario selectedItem = (Scenario) scenariosComboBox.getSelectedItem();
            return selectedItem != null;
        } else {
            return false;
        }
    }

    public boolean runOnChange() {
        return runOnChange;
    }

    public void runOnChange(boolean state) {
        this.runOnChange = state;
    }


    private static class ScenarioNameRenderer extends ListCellRendererWrapper<Scenario> {
        @Override
        public void customize(JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                setText(value.getPresentableText());
            }
        }
    }
}
