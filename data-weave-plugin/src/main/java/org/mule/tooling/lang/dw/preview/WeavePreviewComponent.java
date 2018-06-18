package org.mule.tooling.lang.dw.preview;

import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.execution.ui.layout.actions.RestoreLayoutAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.content.Content;
import com.intellij.util.Alarm;
import org.fest.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.preview.ui.AddInputDialog;
import org.mule.tooling.lang.dw.preview.ui.AddScenarioDialog;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeContextManager;
import org.mule.tooling.lang.dw.service.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentRuntimeManager;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class WeavePreviewComponent implements Disposable {

    private Project myProject;
    private PsiFile currentFile;
    private boolean runOnChange = true;

    private Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private PreviewToolWindowFactory.NameChanger nameChanger;

    private InputsComponent inputsComponent;
    private final OutputComponent outputComponent;
    private PreviewLogsViewer previewLogsViewer;
    private WeaveTreeChangeListener listener;

    private boolean pinned = false;

    public WeavePreviewComponent(Project project) {
        inputsComponent = new InputsComponent();
        outputComponent = new OutputComponent();
        myProject = project;
    }

    public JComponent createComponent() {
        listener = new WeaveTreeChangeListener();
        PsiManager.getInstance(myProject).addPsiTreeChangeListener(listener, this);
        return createPreviewPanel();
    }

    public PsiFile getCurrentFile() {
        return currentFile;
    }


    private JComponent createPreviewPanel() {
        RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(myProject).create("DW-Preview", "DW Preview", myProject.getName(), myProject);
        Content inputsContent = layoutUi.createContent("inputs", createInputComponent(), "Inputs", null, null);
        inputsContent.setCloseable(false);
        inputsContent.setShouldDisposeContent(true);
        layoutUi.addContent(inputsContent, 0, PlaceInGrid.left, false);

        Content outputContent = layoutUi.createContent("output", createOutputComponent(), "Output", AllIcons.General.Information, null);
        outputContent.setCloseable(false);
        outputContent.setShouldDisposeContent(true);
        layoutUi.addContent(outputContent, 0, PlaceInGrid.center, false);

        previewLogsViewer = new PreviewLogsViewer(myProject);
        Content logsContent = layoutUi.createContent("logs", previewLogsViewer, "Logs/Errors", AllIcons.Debugger.Console_log, null);
        logsContent.setShouldDisposeContent(true);

        layoutUi.addContent(logsContent, 0, PlaceInGrid.right, false);

        DefaultActionGroup group = createActionGroup();
        layoutUi.getOptions().setLeftToolbar(group, "unknown");
        return layoutUi.getComponent();
    }

    @NotNull
    private DefaultActionGroup createActionGroup() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AnAction("Add new scenario", "Adds a new scenario for the current mapping", AllIcons.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                WeaveRuntimeContextManager manager = getScenariosManager();
                AddScenarioDialog dialog = new AddScenarioDialog(myProject, manager, currentFile, (scenario) -> {
                    WeaveDocument currentWeaveDocument = getCurrentWeaveDocument();
                    WeaveRuntimeContextManager.getInstance(myProject).setCurrentScenario(currentWeaveDocument, scenario);
                    loadScenario(scenario);
                });
                dialog.show();
            }

            @Override
            public void update(AnActionEvent e) {

            }
        });
        group.add(new ToggleAction(null, "Pin to this mapping", AllIcons.General.Pin_tab) {

            @Override
            public boolean isSelected(AnActionEvent e) {
                return pinned;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                pinned = state;
            }
        });
        group.add(new AnAction("Run", "Execute", AllIcons.General.Run) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                runPreview();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(runAvailable());
            }
        });
        group.add(new ToggleAction(null, "Run on editor changes", AllIcons.Ide.IncomingChangesOn) {

            @Override
            public boolean isSelected(AnActionEvent e) {
                return runOnChange();
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                runOnChange(state);
            }
        });

        group.add(new SelectScenarioAction());
        group.add(createRestoreLayoutAction());
        return group;
    }

    @NotNull
    private RestoreLayoutAction createRestoreLayoutAction() {
        RestoreLayoutAction layoutAction = new RestoreLayoutAction();
        Presentation presentation = layoutAction.getTemplatePresentation();
        presentation.setIcon(AllIcons.Debugger.RestoreLayout);
        presentation.setText("Restore layout");
        return layoutAction;
    }


    @NotNull
    private ComponentWithActions.Impl createInputComponent() {
        JComponent component = inputsComponent.createComponent(myProject);
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AnAction("Add new input", "Adds a new input to the scenario", AllIcons.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                Scenario currentScenarioMaybe = ReadAction.compute(() -> getCurrentScenario());
                WeaveRuntimeContextManager manager = getScenariosManager();
                AddInputDialog dialog = new AddInputDialog(myProject, manager, currentScenarioMaybe, currentFile);
                dialog.show();
            }

            @Override
            public void update(AnActionEvent e) {

            }
        });
        return new ComponentWithActions.Impl(group, null, null, null, component);
    }

    @NotNull
    private ComponentWithActions.Impl createOutputComponent() {
        JComponent component = outputComponent.createComponent(myProject);
        DefaultActionGroup group = outputComponent.createActions();
        return new ComponentWithActions.Impl(group, null, null, null, component);
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
        outputComponent.setCurrentFile(psiFile);
        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
        WeaveRuntimeContextManager instance = getScenariosManager();
        List<Scenario> scenarios = instance.getScenariosFor(weaveDocument);

        if (scenarios.isEmpty()) {
            listener.doRunPreviewWithoutScenario();
        } else {
            //Load first scenario
            Scenario currentScenarioFor = instance.getCurrentScenarioFor(weaveDocument);
            if (currentScenarioFor == null) {
                return;
            }
            loadScenario(currentScenarioFor);
        }
    }


    public void loadScenario(Scenario scenario) {
        if (scenario == null || !scenario.isValid()) {
            return;
        }

        if (nameChanger != null) {
            String presentableText = scenario.getPresentableText();
            nameChanger.changeName("Scenario: " + presentableText);
        }
        final VirtualFile inputs = scenario.getInputs();
        if (inputs != null && inputs.isDirectory()) {
            inputsComponent.loadInputFiles(inputs);
        } else {
            inputsComponent.closeAllInputs();
        }
        listener.doRunPreview();
    }

    public void clearScenario() {
        if (nameChanger != null) {
            nameChanger.changeName(WeaveConstants.NO_SCENARIO);
        }
        inputsComponent.closeAllInputs();
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

    public void runPreview(Scenario scenario, PsiFile currentFile) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (scenario != null && scenario.getInputs() != null) {
                VirtualFile inputs = Objects.requireNonNull(scenario.getInputs());
                runPreviewWithInputs(inputs.getPath(), currentFile);
            } else {
                runPreviewWithoutInputs(currentFile);
            }
        });
    }

    private void runPreviewWithoutInputs(PsiFile currentFile) {
        runPreviewWithInputs("", currentFile);
    }

    private void runPreviewWithInputs(String inputsPath, PsiFile currentFile) {
        final Document document = PsiDocumentManager.getInstance(myProject).getDocument(currentFile);
        if (document == null) return;
        //IMPORTANT NOTE: sometimes our current WeaveDocument is not updated correctly, so always get text from Document
        String text = ReadAction.compute(document::getText);
        String documentQName = ReadAction.compute(() -> getCurrentWeaveDocument().getQualifiedName());
        String url = ReadAction.compute(() -> currentFile.getVirtualFile().getUrl());
        final Module module = ModuleUtil.findModuleForFile(currentFile.getVirtualFile(), myProject);

        final WeaveAgentRuntimeManager agentComponent = WeaveAgentRuntimeManager.getInstance(myProject);
        agentComponent.runPreview(inputsPath, text, documentQName, url, 10000L, module, new MyRunPreviewCallback());
    }


    public WeaveDocument getCurrentWeaveDocument() {
        return WeavePsiUtils.getWeaveDocument(currentFile);
    }

    private WeaveRuntimeContextManager getScenariosManager() {
        return WeaveRuntimeContextManager.getInstance(myProject);
    }

    private Scenario getCurrentScenario() {
        return getScenariosManager().getCurrentScenarioFor(getCurrentWeaveDocument());
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
        this.nameChanger = callback;
    }


//    private static class ScenarioNameRenderer extends ListCellRendererWrapper<Scenario> {
//        @Override
//        public void customize(JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
//            if (value != null) {
//                setText(value.getPresentableText());
//            }
//        }
//    }

    private boolean isOutputFile(PsiFile psiFile) {
        return psiFile.getVirtualFile().getNameWithoutExtension().equals("out");
    }

    /**
     * This listener runs the preview each time a change occurred in the PSI tree
     */
    private class WeaveTreeChangeListener extends PsiTreeChangeAdapter {
        private boolean isRelevantEvent(PsiTreeChangeEvent event) {
            PsiFile file = event.getFile();
            if (file != null) {
                //change happened inside a PsiFile
                return !isOutputFile(file);
            } else {
                //change happened in a directory, or something that isn't a PsiFile
                PsiElement child = event.getChild();
                if (child instanceof PsiFile) {
                    PsiFile psiFile = (PsiFile) child;
                    // return true if added or removed a file (other than the expected output)
                    return !psiFile.isDirectory() && !isOutputFile(psiFile);
                } else {
                    return false;
                }
            }
        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event) && event.getFile() == null) {
                loadScenario(getCurrentScenario());
            }
        }

        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            if (event.getFile() == null) {
                //change didn't happen inside a PsiFile
                Scenario currentScenario = getCurrentScenario();
                if (currentScenario != null && currentScenario.isValid()) {
                    loadScenario(currentScenario);
                } else {
                    clearScenario();
                }
            }
        }

        @Override
        public void childMoved(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) {
                doRunPreview();
            }
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) {
                doRunPreview();
            }
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent event) {
            if (isRelevantEvent(event)) {
                doRunPreview();
            }
        }

        @Override
        public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
            super.propertyChanged(event);
            String propertyName = event.getPropertyName();
            if (propertyName.equals(PsiTreeChangeEvent.PROP_FILE_NAME) || propertyName.equals(PsiTreeChangeEvent.PROP_DIRECTORY_NAME)) {
                loadScenario(getCurrentScenario());
            }
        }

        public void doRunPreview() {
            doRunPreview(getCurrentScenario(), currentFile);
        }

        public void doRunPreviewWithoutScenario() {
            myDocumentAlarm.cancelAllRequests();
            myDocumentAlarm.addRequest(() -> {
                if (myDocumentAlarm.isDisposed()) {
                    return;
                }
                ApplicationManager.getApplication().executeOnPooledThread(() ->
                        runPreviewWithoutInputs(currentFile)
                );

            }, WeaveConstants.MODIFICATIONS_DELAY);
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

    @NotNull
    private List<Scenario> getScenarios(@Nullable PsiFile selectedPsiFile) {
        if (selectedPsiFile == null) {
            return Lists.newArrayList();
        }
        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(selectedPsiFile);
        WeaveRuntimeContextManager instance = WeaveRuntimeContextManager.getInstance(myProject);
        return instance.getScenariosFor(weaveDocument);
    }

    @Nullable
    private PsiFile getSelectedPsiFile() {
        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        return files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]);
    }


    private class SelectScenarioAction extends AnAction {
        public SelectScenarioAction() {
            super(null, "Select scenario", AllIcons.General.Gear);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            DefaultActionGroup group = new DefaultActionGroup();
            List<Scenario> scenarios = getScenarios(getSelectedPsiFile());

            addScenarioActions(group, scenarios);

            final InputEvent inputEvent = e.getInputEvent();
            final ActionPopupMenu popupMenu =
                    ((ActionManagerImpl) ActionManager.getInstance())
                            .createActionPopupMenu(ToolWindowContentUi.POPUP_PLACE, group, new MenuItemPresentationFactory(true));

            int x = 0;
            int y = 0;
            if (inputEvent instanceof MouseEvent) {
                x = ((MouseEvent) inputEvent).getX();
                y = ((MouseEvent) inputEvent).getY();
            }
            popupMenu.getComponent().show(inputEvent.getComponent(), x, y);
        }

        private void addScenarioActions(DefaultActionGroup group, List<Scenario> scenarios) {
            for (Scenario scenario : scenarios) {
                group.add(new AnAction(scenario.getPresentableText(), scenario.getLocationString(), null) {
                    @Override
                    public void actionPerformed(AnActionEvent e) {
                        WeaveDocument currentWeaveDocument = getCurrentWeaveDocument();
                        WeaveRuntimeContextManager.getInstance(myProject).setCurrentScenario(currentWeaveDocument, scenario);
                        loadScenario(scenario);
                    }
                });
            }

        }
    }

    private class MyRunPreviewCallback implements RunPreviewCallback {

        @Override
        public void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result) {
            Scenario scenario = getCurrentScenario();
            final VirtualFile file = getOutputFile(scenario);
            outputComponent.onPreviewResult(result, file);
            previewLogsViewer.clear();
            previewLogsViewer.logInfo(ScalaUtils.toList(result.messages()));
        }

        @Override
        public void onPreviewFailed(PreviewExecutedFailedEvent message) {
            previewLogsViewer.clear();
            previewLogsViewer.logInfo(ScalaUtils.toList(message.messages()));
            previewLogsViewer.logError(message.message());
        }
    }

    @Nullable
    private VirtualFile getOutputFile(Scenario scenario) {
        if (scenario != null) {
            return scenario.getOutput().orElse(null);
        } else {
            return null;
        }
    }
}
