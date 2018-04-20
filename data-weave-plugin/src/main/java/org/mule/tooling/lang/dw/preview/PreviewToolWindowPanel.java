package org.mule.tooling.lang.dw.preview;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
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
    //TODO we should put this in a settings file

    private Project myProject;

    private WeavePreviewComponent weavePreviewComponent;
    private JPanel mainPanel;
    private CardLayout cardLayout;


    private JComponent previewComponent;

    public PreviewToolWindowPanel(Project project) {
        super(false);
        this.myProject = project;
        setupUI();
        initFileListener();
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        JPanel noLiveViewAvailable = createNoLiveViewAvailable();
        weavePreviewComponent = new WeavePreviewComponent();
        mainPanel.add(noLiveViewAvailable, NOTHING_TO_SHOW);

        setContent(mainPanel);
    }

    private void initFileListener() {

        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        setFile(files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]));

        myProject.getMessageBus().connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent e) {
                if (WeaveAgentComponent.getInstance(myProject).isWeaveRuntimeInstalled()) {
                    VirtualFile file = e.getNewFile();
                    final PsiFile psiFile = file != null && file.isValid() ? PsiManager.getInstance(myProject).findFile(file) : null;
                    // This invokeLater is required. The problem is setFile does a commit to PSI, but setFile is
                    // invoked inside PSI change event. It causes an Exception like "Changes to PSI are not allowed inside event processing"
                    DumbService.getInstance(myProject).smartInvokeLater(() -> setFile(psiFile));
                }
            }
        });
    }

    private JPanel createNoLiveViewAvailable() {
        return new MessagePanel("No live view available.");
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
            weavePreviewComponent.setFile(psiFile);
            cardLayout.show(mainPanel, PREVIEW_EDITOR);
        } else {
            //If It didn't returned then set the nothing to show message
            cardLayout.show(mainPanel, NOTHING_TO_SHOW);
        }
    }


    @Override
    public void dispose() {
        if (weavePreviewComponent != null) {
            weavePreviewComponent.close();
            weavePreviewComponent = null;
        }
    }
}
