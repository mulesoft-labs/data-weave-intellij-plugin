package org.mule.tooling.lang.dw.develop_view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class DevelopToolingWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(new AstGraphToolingWindowPanel(project), "AST", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(new VariableGraphToolingWindowPanel(project), "Scope Graph", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(new TypeGraphToolingWindowPanel(project), "Type Graph", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(new DependencyGraphToolingWindowPanel(project), "Dependency Graph", false));
  }

  @Override
  public void init(ToolWindow window) {

  }

  @Override
  public boolean shouldBeAvailable(@NotNull Project project) {
    return true;
  }
}
