package org.mule.tooling.lang.dw.preview;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class PreviewToolWindowFactory implements ToolWindowFactory, DumbAware {

    public static String ID = "Weave Preview";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = getContentFactory();
        Content preview = contentFactory.createContent(new PreviewToolWindowPanel(project), "Preview", false);
        toolWindow.getContentManager().addContent(preview);
    }

    private ContentFactory getContentFactory() {
        return ContentFactory.SERVICE.getInstance();
    }

}
