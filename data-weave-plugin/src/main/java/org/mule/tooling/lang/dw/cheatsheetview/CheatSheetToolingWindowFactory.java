package org.mule.tooling.lang.dw.cheatsheetview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.develop_view.AstGraphToolingWindowPanel;
import org.mule.tooling.lang.dw.develop_view.TypeGraphToolingWindowPanel;
import org.mule.tooling.lang.dw.develop_view.VariableGraphToolingWindowPanel;

public class CheatSheetToolingWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(new CheatSheetToolingWindowPanel(), "CheatSheet", false));
  }

  @Override
  public void init(ToolWindow window) {

  }

  @Override
  public boolean shouldBeAvailable(@NotNull Project project) {
    return true;
  }
}
