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
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.psi.PsiDocumentManager;
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
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.agent.RunPreviewCallback;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class WeavePreviewComponent implements Disposable {

    private Project myProject;
    private PsiFile currentFile;
    private boolean runOnChange = true;

    private Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private PreviewToolWindowFactory.NameChanger callback;

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
                DataWeaveScenariosManager manager = getScenariosManager();
                AddInputDialog dialog = new AddInputDialog(myProject, manager, currentScenarioMaybe, currentFile);
                dialog.show();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(runAvailable());
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

    public void runPreview(Scenario scenario, PsiFile currentFile) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (scenario == null) return;
            final Document document = PsiDocumentManager.getInstance(myProject).getDocument(currentFile);
            WeaveDocument weaveDocument = ReadAction.compute(() -> getCurrentWeaveDocument());
            String documentQName = ReadAction.compute(weaveDocument::getQualifiedName);
            final WeaveAgentComponent agentComponent = WeaveAgentComponent.getInstance(myProject);
            VirtualFile inputs = scenario.getInputs();
            if (inputs == null) return;

            final String inputsPath = inputs.getPath();
            final Module module = ModuleUtil.findModuleForFile(currentFile.getVirtualFile(), myProject);
            String url = ReadAction.compute(() -> currentFile.getVirtualFile().getUrl());

            if (document == null) return;
            //IMPORTANT NOTE: sometimes our current WeaveDocument is not updated correctly, so always get text from Document
            String text = ReadAction.compute(document::getText);

            agentComponent.runPreview(inputsPath, text, documentQName, url, 10000L, module, new RunPreviewCallback() {

                @Override
                public void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result) {
                    Scenario scenario = getCurrentScenario();
                    VirtualFile file = scenario.getOutput().orElse(null);
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
            });

        });


    }

    private Scenario getCurrentScenario() {
        return getScenariosManager().getCurrentScenarioFor(getCurrentWeaveDocument());
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


//    private static class ScenarioNameRenderer extends ListCellRendererWrapper<Scenario> {
//        @Override
//        public void customize(JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
//            if (value != null) {
//                setText(value.getPresentableText());
//            }
//        }
//    }

    /**
     * This listener runs the preview each time a change occurred in the PSI tree
     */
    private class WeaveTreeChangeListener extends PsiTreeChangeAdapter {
        private boolean isRelevantEvent(PsiTreeChangeEvent event) {
            PsiFile file = event.getFile();
            return file != null && !file.getVirtualFile().getNameWithoutExtension().equals("out");
        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            if (!isRelevantEvent(event)) return;
            super.childAdded(event);
            loadScenario(getCurrentScenario());
        }

        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            if (!isRelevantEvent(event)) return;
            super.childRemoved(event);
            loadScenario(getCurrentScenario());
        }

        @Override
        public void childMoved(@NotNull PsiTreeChangeEvent event) {
            if (!isRelevantEvent(event)) return;
            super.childMoved(event);
            doRunPreview();
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
            if (!isRelevantEvent(event)) return;
            super.childrenChanged(event);
            doRunPreview();
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent event) {
            if (!isRelevantEvent(event)) return;
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

    @NotNull
    private List<Scenario> getScenarios(@Nullable PsiFile selectedPsiFile) {
        if (selectedPsiFile == null) {
            return Lists.newArrayList();
        }
        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(selectedPsiFile);
        DataWeaveScenariosManager instance = DataWeaveScenariosManager.getInstance(myProject);
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
                        DataWeaveScenariosManager.getInstance(myProject).setCurrentScenario(currentWeaveDocument, scenario);
                        loadScenario(scenario);
                    }
                });
            }

        }
    }
}
