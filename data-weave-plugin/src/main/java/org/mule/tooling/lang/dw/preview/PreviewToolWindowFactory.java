package org.mule.tooling.lang.dw.preview;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeContextManager;

public class PreviewToolWindowFactory implements ToolWindowFactory {

    public static String ID = "Weave Preview";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        String displayName = getDisplayName(project);

        ContentFactory contentFactory = getContentFactory();
        PreviewToolWindowPanel previewToolWindowPanel = new PreviewToolWindowPanel(project);
        Content preview = contentFactory.createContent(previewToolWindowPanel, displayName, false);
        previewToolWindowPanel.setNameChanger(preview::setDisplayName);
        toolWindow.getContentManager().addContent(preview);
    }

    @NotNull
    private String getDisplayName(@NotNull Project project) {
        PsiFile selectedPsiFile = getSelectedPsiFile(project);
        if (selectedPsiFile != null) {
            WeaveDocument currentWeaveDocument = WeavePsiUtils.getWeaveDocument(selectedPsiFile);
            Scenario scenario = WeaveRuntimeContextManager.getInstance(project).getCurrentScenarioFor(currentWeaveDocument);
            if (scenario != null) {
                return "Scenario: " + String.valueOf(scenario.getPresentableText());
            }
        }
        return WeaveConstants.NO_SCENARIO;
    }

    private ContentFactory getContentFactory() {
        return ContentFactory.SERVICE.getInstance();
    }

    @Nullable
    private PsiFile getSelectedPsiFile(Project myProject) {
        VirtualFile[] files = FileEditorManager.getInstance(myProject).getSelectedFiles();
        return files.length == 0 ? null : PsiManager.getInstance(myProject).findFile(files[0]);
    }

    public interface NameChanger {
        void changeName(String name);
    }
}
