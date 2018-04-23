package org.mule.tooling.lang.dw.preview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.agent.WeaveAgentComponent;
import org.mule.tooling.lang.dw.ui.MessagePanel;

import javax.swing.*;
import java.awt.*;

public class PreviewToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    public static final String NOTHING_TO_SHOW = "NOTHING_TO_SHOW";
    public static final String PREVIEW_EDITOR = "PREVIEW_EDITOR";
    public static final String NO_RUNTIME_AVAILABLE = "NO_RUNTIME_AVAILABLE";
    //TODO we should put this in a settings file

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
        weavePreviewComponent = new WeavePreviewComponent();
        mainPanel.add(new MessagePanel("No DataWeave runtime found."), NO_RUNTIME_AVAILABLE);
        mainPanel.add(new MessagePanel("No live view available."), NOTHING_TO_SHOW);
        setContent(mainPanel);
        setToolbar(createToolbar());
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

        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar actionToolBar = actionManager.createActionToolbar(PreviewToolWindowFactory.ID, group, false);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }


    private void initFileListener() {

        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        setFile(files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]));

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


    private void setFile(@Nullable PsiFile psiFile) {
        cardLayout.show(mainPanel, NOTHING_TO_SHOW);
        if (previewComponent != null) {
            weavePreviewComponent.close();
            mainPanel.remove(previewComponent);
        }
        if (psiFile != null && psiFile.getFileType() == WeaveFileType.getInstance()) {
            previewComponent = weavePreviewComponent.createComponent(myProject);
            mainPanel.add(previewComponent, PREVIEW_EDITOR);
            weavePreviewComponent.open(psiFile);
            cardLayout.show(mainPanel, PREVIEW_EDITOR);
        } else {
            //If It didn't returned then set the nothing to show message
            cardLayout.show(mainPanel, NOTHING_TO_SHOW);
        }
    }


    @Override
    public void dispose() {
        if (weavePreviewComponent != null) {
            Disposer.dispose(weavePreviewComponent);
            weavePreviewComponent = null;
        }
    }
}
