package org.mule.tooling.lang.dw.preview;

import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.progress.util.ReadTask;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.content.Content;
import com.intellij.util.Alarm;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.util.List;

public class WeavePreviewComponent implements Disposable, DumbAware {

    private Project myProject;
    private PsiFile currentFile;
    private boolean runOnChange = true;

    private Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private PreviewToolWindowFactory.NameChanger callback;

    private InputsComponent inputsComponent;
    private final OutputComponent outputComponent;
    private PreviewLogsViewer previewLogsViewer;
    private WeaveTreeChangeListener listener;


    public WeavePreviewComponent() {
        inputsComponent = new InputsComponent();
        outputComponent = new OutputComponent();
    }

    public JComponent createComponent(Project project) {
        myProject = project;

        listener = new WeaveTreeChangeListener();
        PsiManager.getInstance(project).addPsiTreeChangeListener(listener, this);

        return createPreviewPanel();
    }

    public PsiFile getCurrentFile() {
        return currentFile;
    }


    private JComponent createPreviewPanel() {
        BorderLayoutPanel previewPanel = new BorderLayoutPanel();


        RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(myProject).create("DW-Preview", "DW Preview", myProject.getName(), myProject);
        Content inputsContent = layoutUi.createContent("inputs", createInputComponent(), "Inputs", null, null);
        inputsContent.setCloseable(false);

        layoutUi.addContent(inputsContent, 0, PlaceInGrid.left, false);
        Content outputContent = layoutUi.createContent("output", outputComponent.createComponent(myProject), "Output", AllIcons.General.Information, null);
        outputContent.setCloseable(false);
        layoutUi.addContent(outputContent, 1, PlaceInGrid.right, false);

        previewLogsViewer = new PreviewLogsViewer(myProject);
        Content logsContent = layoutUi.createContent("logs", previewLogsViewer, "Logs/Errors", AllIcons.Debugger.Console_log, null);
        layoutUi.addContent(logsContent, 2, PlaceInGrid.right, false);

        previewPanel.addToCenter(layoutUi.getComponent());
        return previewPanel;
    }

    @NotNull
    private ComponentWithActions.Impl createInputComponent() {
        JComponent component = inputsComponent.createComponent(myProject);
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AnAction("Add new input", "Adds a new input to the scenario", AllIcons.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {

                ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiFile currentFile = getCurrentFile();
                    DataWeaveScenariosManager manager = getScenariosManager();
                    WeaveDocument document = getCurrentWeaveDocument();
                    Scenario currentScenario = manager.getCurrentScenarioFor(document);

                    //todo: prompt user for an input name
                    String inputName = "payload.json";

                    if (currentScenario == null || !currentScenario.isValid()) {
                        //create scenario
                        VirtualFile scenarioFolder = manager.createScenario(currentFile);
                        currentScenario = new Scenario(scenarioFolder);
                        manager.setCurrentScenario(document, currentScenario);
                    }
                    VirtualFile inputFile = currentScenario.addInput(inputName);
                    setFileContent(inputFile, "{}");

                    System.out.println("WeavePreviewComponent.addNewInput");
                });

            }

            @Override
            public void update(AnActionEvent e) {
                super.update(e);
//                setEnabled(weavePreviewComponent.runAvailable());
            }
        });
        return new ComponentWithActions.Impl(group, null, null, null, component);
    }

    private void setFileContent(VirtualFile inputFile, String content) {
        if (inputFile == null) {
            return;
        }
        if (!inputFile.isValid()) {
            System.out.println("Input file is invalid. " + inputFile.toString());
            return;
        }

        try {
            inputFile.setBinaryContent(content.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Sets the current PsiFile and loads its first scenario
     */
    public void open(@Nullable PsiFile psiFile) {
        if (currentFile == psiFile) {
            return;
        }
        if (psiFile == null || psiFile.getFileType() != WeaveFileType.getInstance()) {
            return;
        }

        this.currentFile = psiFile;
        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
        DataWeaveScenariosManager instance = getScenariosManager();
        List<Scenario> scenarios = instance.getScenariosFor(weaveDocument);

        //Load first scenario
        if (scenarios.isEmpty()) {
            return;
        }
        Scenario currentScenarioFor = instance.getCurrentScenarioFor(weaveDocument);
        if (currentScenarioFor == null) {
            return;
        }
        loadScenario(currentScenarioFor);
    }


    public WeaveDocument getCurrentWeaveDocument() {
        return WeavePsiUtils.getWeaveDocument(currentFile);
    }

    public void loadScenario(Scenario scenario) {
        if (scenario == null) {
            return;
        }

        if (callback != null) {
            String presentableText = scenario.getPresentableText();
            callback.changeName("Scenario: " + presentableText);
        }
        final VirtualFile inputs = scenario.getInputs();
        if (inputs != null && inputs.isDirectory()) {
            inputsComponent.loadInputFiles(inputs);
        } else {
            inputsComponent.closeAllInputs();
        }
        listener.doRunPreview();
    }

    @Override
    public void dispose() {
        this.currentFile = null;

        inputsComponent.dispose();
        outputComponent.dispose();
        previewLogsViewer.dispose();
        PsiManager.getInstance(myProject).removePsiTreeChangeListener(listener);
    }

    public void runPreview() {
        Scenario currentScenario = getCurrentScenario();
        runPreview(currentScenario, currentFile);
    }

    public void runPreview(Scenario selectedItem, PsiFile currentFile) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (selectedItem == null)
                return;
            WeaveDocument document = ReadAction.compute(() -> getCurrentWeaveDocument());
            String documentQName = ReadAction.compute(document::getQualifiedName);
            final WeaveAgentComponent agentComponent = WeaveAgentComponent.getInstance(myProject);
            final String inputsPath = selectedItem.getInputs().getPath();
            final Module module = ModuleUtil.findModuleForFile(currentFile.getVirtualFile(), myProject);
            String url = ReadAction.compute(() -> currentFile.getVirtualFile().getUrl());
            String text = ReadAction.compute(document::getText);

            agentComponent.runPreview(inputsPath, text, documentQName, url, 10000L, module, new RunPreviewCallback() {

                @Override
                public void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result) {
                    outputComponent.onPreviewResult(result);
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

    private Scenario getCurrentScenario() {
        WeaveDocument currentWeaveDocument = getCurrentWeaveDocument();
        return getScenariosManager().getCurrentScenarioFor(currentWeaveDocument);
    }

    private DataWeaveScenariosManager getScenariosManager() {
        return DataWeaveScenariosManager.getInstance(myProject);
    }

    public boolean runAvailable() {
        return currentFile != null && getCurrentScenario() != null;
    }

    public boolean runOnChange() {
        return runOnChange;
    }

    public void runOnChange(boolean state) {
        this.runOnChange = state;
    }

    public void setNameChanger(PreviewToolWindowFactory.NameChanger callback) {
        this.callback = callback;
    }


    private static class ScenarioNameRenderer extends ListCellRendererWrapper<Scenario> {
        @Override
        public void customize(JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                setText(value.getPresentableText());
            }
        }
    }

    /**
     * This listener runs the preview each time a change occurred in the PSI tree
     */
    private class WeaveTreeChangeListener extends PsiTreeChangeAdapter {
        private boolean isRelevantEvent(PsiTreeChangeEvent event) {
            return !isFileEvent(event);
        }

        private boolean isFileEvent(PsiTreeChangeEvent event) {
            PsiFile file = event.getFile();
            return file != null && file.isDirectory();
        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) return;
            super.childAdded(event);
            loadScenario(getCurrentScenario());
//            doRunPreview();
        }

        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) return;
            super.childRemoved(event);
            loadScenario(getCurrentScenario());
//            doRunPreview();
        }

        @Override
        public void childMoved(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) return;
            super.childMoved(event);
            doRunPreview();
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
//            if (isRelevantEvent(event)) return;
            super.childrenChanged(event);
            doRunPreview();
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) return;
            doRunPreview();

            //We know the change came from this file now
        }

        public void doRunPreview() {
            doRunPreview(getCurrentScenario(), currentFile);
        }

        public void doRunPreview(Scenario scenario, PsiFile psiFile) {
            if (currentFile == null || !runOnChange) {
                return;
            }

            //We call all the request and add a new one if in 200 milliseconds no change was introduced then trigger preview
            myDocumentAlarm.cancelAllRequests();
            myDocumentAlarm.addRequest(() -> {
                if (myDocumentAlarm.isDisposed()) {
                    return;
                }
                runPreview(scenario, psiFile);

            }, WeaveConstants.MODIFICATIONS_DELAY);
        }
    }
}
