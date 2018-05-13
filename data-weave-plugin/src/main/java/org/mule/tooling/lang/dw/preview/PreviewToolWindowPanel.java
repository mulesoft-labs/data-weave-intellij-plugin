package org.mule.tooling.lang.dw.preview;

import com.intellij.icons.AllIcons;
import com.intellij.internal.statistic.customUsageCollectors.ui.ToolbarClicksCollector;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.fest.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.ui.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class PreviewToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    public static final String NOTHING_TO_SHOW = "NOTHING_TO_SHOW";
    public static final String PREVIEW_EDITOR = "PREVIEW_EDITOR";
    public static final String NO_RUNTIME_AVAILABLE = "NO_RUNTIME_AVAILABLE";
    //TODO we should put this in a settings file

    private PreviewToolWindowFactory.NameChanger callback;
    private Project myProject;
    private WeavePreviewComponent weavePreviewComponent;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JComponent previewComponent;
    private boolean pinned = false;

    public PreviewToolWindowPanel(Project project) {
        super(false);
        this.myProject = project;
        setupUI();
        initFileListener();
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new MessagePanel("No DataWeave runtime found."), NO_RUNTIME_AVAILABLE);
        mainPanel.add(new MessagePanel("No live view available."), NOTHING_TO_SHOW);
        weavePreviewComponent = new WeavePreviewComponent();
        previewComponent = weavePreviewComponent.createComponent(myProject);
        mainPanel.add(previewComponent, PREVIEW_EDITOR);
        setContent(mainPanel);
        setToolbar(createToolbar());
        cardLayout.show(mainPanel, NOTHING_TO_SHOW);
    }

    private BorderLayoutPanel createToolbar() {
        final DefaultActionGroup group = new DefaultActionGroup();
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
                weavePreviewComponent.runPreview();
            }

            @Override
            public void update(AnActionEvent e) {
                super.update(e);
                setEnabled(weavePreviewComponent.runAvailable());
            }
        });
        group.add(new ToggleAction(null, "Run on editor changes", AllIcons.Ide.IncomingChangesOn) {

            @Override
            public boolean isSelected(AnActionEvent e) {
                return weavePreviewComponent.runOnChange();
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                weavePreviewComponent.runOnChange(state);
            }
        });

        group.add(new SelectScenarioAction(getScenarios(getSelectedPsiFile())));

        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar actionToolBar = actionManager.createActionToolbar(PreviewToolWindowFactory.ID, group, false);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
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


    private void initFileListener() {
        setFile(getSelectedPsiFile());

        myProject.getMessageBus().connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent e) {
                if (WeaveAgentComponent.getInstance(myProject).isWeaveRuntimeInstalled()) {
                    if (!pinned) {
                        VirtualFile file = e.getNewFile();
                        final PsiFile psiFile = file != null && file.isValid() ? PsiManager.getInstance(myProject).findFile(file) : null;
                        // This invokeLater is required. The problem is open does a commit to PSI, but open is
                        // invoked inside PSI change event. It causes an Exception like "Changes to PSI are not allowed inside event processing"
                        DumbService.getInstance(myProject).smartInvokeLater(() -> setFile(psiFile));
                    }
                } else {
                    cardLayout.show(mainPanel, NO_RUNTIME_AVAILABLE);
                }
            }
        });
    }

    @Nullable
    private PsiFile getSelectedPsiFile() {
        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        return files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]);
    }


    private void setFile(@Nullable PsiFile psiFile) {
        if (psiFile == null) {
            cardLayout.show(mainPanel, NOTHING_TO_SHOW);
        } else if (psiFile != weavePreviewComponent.getCurrentFile() && psiFile.getFileType() == WeaveFileType.getInstance()) {
            WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            if (weaveDocument != null && weaveDocument.isMappingDocument()) {
                if (previewComponent != null) {
                    weavePreviewComponent.dispose();
                    mainPanel.remove(previewComponent);
                    previewComponent = null;
                }
                previewComponent = weavePreviewComponent.createComponent(myProject);
                mainPanel.add(previewComponent, PREVIEW_EDITOR);
                weavePreviewComponent.open(psiFile);
                cardLayout.show(mainPanel, PREVIEW_EDITOR);
            }
        }
    }


    @Override
    public void dispose() {
        if (weavePreviewComponent != null) {
            Disposer.dispose(weavePreviewComponent);
            weavePreviewComponent = null;
        }
    }


    private class SelectScenarioAction extends AnAction {
        private List<Scenario> scenarios;
        public SelectScenarioAction(List<Scenario> scenarios) {
            super(null, "Select scenario", AllIcons.General.Gear);
            this.scenarios = scenarios;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            DefaultActionGroup group = new DefaultActionGroup();

            addScenarioActions(group, scenarios);

            final InputEvent inputEvent = e.getInputEvent();
            final ActionPopupMenu popupMenu =
                    ((ActionManagerImpl)ActionManager.getInstance())
                            .createActionPopupMenu(ToolWindowContentUi.POPUP_PLACE, group, new MenuItemPresentationFactory(true));

            int x = 0;
            int y = 0;
            if (inputEvent instanceof MouseEvent) {
                x = ((MouseEvent)inputEvent).getX();
                y = ((MouseEvent)inputEvent).getY();
            }
            ToolbarClicksCollector.record("Show Options", "ToolWindowHeader");
            popupMenu.getComponent().show(inputEvent.getComponent(), x, y);
        }

        private void addScenarioActions(DefaultActionGroup group, List<Scenario> scenarios) {
            for(Scenario scenario : scenarios) {
                group.add(new AnAction(scenario.getPresentableText(), scenario.getLocationString(), null) {
                    @Override
                    public void actionPerformed(AnActionEvent e) {
                        WeaveDocument currentWeaveDocument = weavePreviewComponent.getCurrentWeaveDocument();
                        DataWeaveScenariosManager.getInstance(myProject).setCurrentScenario(currentWeaveDocument, scenario);
                        weavePreviewComponent.loadScenario(scenario);
                    }
                });
            }

        }
    }

    public void setNameChanger(PreviewToolWindowFactory.NameChanger callback) {
        weavePreviewComponent.setNameChanger(callback);
    }
}
