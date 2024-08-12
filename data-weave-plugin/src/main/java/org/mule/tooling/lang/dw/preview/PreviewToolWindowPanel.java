package org.mule.tooling.lang.dw.preview;

import com.intellij.ProjectTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.CancellablePromise;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentService;
import org.mule.tooling.lang.dw.ui.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;

import static com.intellij.openapi.application.ReadAction.nonBlocking;

public class PreviewToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    public static final String NOTHING_TO_SHOW = "NOTHING_TO_SHOW";
    public static final String PREVIEW_EDITOR = "PREVIEW_EDITOR";
    public static final String NO_RUNTIME_AVAILABLE = "NO_RUNTIME_AVAILABLE";
    //TODO we should put this in a settings file

    private @NotNull ExecutorService dependenciesChanges = AppExecutorUtil.createBoundedApplicationPoolExecutor("RootChanges", 15);

    private Project myProject;
    private WeavePreviewComponent weavePreviewComponent;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JComponent previewComponent;

    private WeaveAgentService agentRuntimeManager;

    public PreviewToolWindowPanel(Project project) {
        super(false);
        this.myProject = project;
        this.agentRuntimeManager = WeaveAgentService.getInstance(myProject);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new MessagePanel("No DataWeave runtime found."), NO_RUNTIME_AVAILABLE);
        mainPanel.add(new MessagePanel("No live view available."), NOTHING_TO_SHOW);
        weavePreviewComponent = new WeavePreviewComponent(project);
        DumbService.getInstance(myProject).smartInvokeLater(() -> {
            setupUI(project);
            initFileListener();
        });
    }

    private void setupUI(Project project) {
        previewComponent = weavePreviewComponent.createComponent();
        mainPanel.add(previewComponent, PREVIEW_EDITOR);
        cardLayout.show(mainPanel, NOTHING_TO_SHOW);
        setContent(mainPanel);
    }

    private void initFileListener() {
        CancellablePromise<PsiFile> selectedFilePromise = nonBlocking(() -> {
            return getSelectedPsiFile();
        }).submit(AppExecutorUtil.getAppExecutorService());
        selectedFilePromise
                .onSuccess((selectedPsiFile) -> {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        setFile(selectedPsiFile);
                        listenFileChanges();
                    });
                });
    }

    private void listenFileChanges() {
        final MessageBusConnection[] connection = {null};

        myProject.getMessageBus().connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {

            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                if (!weavePreviewComponent.isPinned()) {
                    final PsiFile psiFile = file.isValid() ? PsiManager.getInstance(myProject).findFile(file) : null;
                    // This invokeLater is required. The problem is open does a commit to PSI, but open is
                    // invoked inside PSI change event. It causes an Exception like "Changes to PSI are not allowed inside event processing"
                    DumbService.getInstance(myProject).smartInvokeLater(() -> {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            setFile(psiFile);
                        });
                    });
                }
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent e) {

                if (agentRuntimeManager.isWeaveRuntimeInstalled()) {
                    if (connection[0] != null) {
                        connection[0].disconnect();
                        connection[0] = null;
                    }
                    showFile(e);
                } else {
                    cardLayout.show(mainPanel, NO_RUNTIME_AVAILABLE);

                    //Once agent is available we need to reset the UI.
                    if (connection[0] != null) {
                        connection[0].disconnect();
                        connection[0] = null;
                    }

                    connection[0] = myProject.getMessageBus().connect(PreviewToolWindowPanel.this);
                    connection[0].subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
                        @Override
                        public void rootsChanged(ModuleRootEvent event) {
                            dependenciesChanges.submit(() -> {
                                if (agentRuntimeManager.isWeaveRuntimeInstalled()) {
                                    showFile(e);
                                    connection[0].disconnect();
                                    connection[0] = null;
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void showFile(@NotNull FileEditorManagerEvent e) {
        if (!weavePreviewComponent.isPinned()) {
            VirtualFile file = e.getNewFile();
            final PsiFile psiFile = ReadAction.compute(() -> file != null && file.isValid() ? PsiManager.getInstance(myProject).findFile(file) : null);
            // This invokeLater is required. The problem is open does a commit to PSI, but open is
            // invoked inside PSI change event. It causes an Exception like "Changes to PSI are not allowed inside event processing"
            DumbService.getInstance(myProject).smartInvokeLater(() -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    setFile(psiFile);
                });

            });
        }
    }

    @Nullable
    private PsiFile getSelectedPsiFile() {
        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        return files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]);
    }


    private void setFile(@Nullable PsiFile psiFile) {
        if (psiFile == null) {
            cardLayout.show(mainPanel, NOTHING_TO_SHOW);
            // Dispose the wavePreviewComponent when dwl file is closed, so that the component can open when dwl is reopened
            weavePreviewComponent.close();
        } else if (psiFile != weavePreviewComponent.getCurrentFile() && psiFile.getFileType() == WeaveFileType.getInstance()) {
            WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            if (weaveDocument != null && weaveDocument.isMappingDocument()) {
                if (previewComponent != null) {
                    mainPanel.remove(previewComponent);
                    previewComponent = null;
                }
                previewComponent = weavePreviewComponent.createComponent();
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

    public void setNameChanger(PreviewToolWindowFactory.NameChanger nameChanger) {
        weavePreviewComponent.setNameChanger(nameChanger);
    }
}
