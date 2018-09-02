package org.mule.tooling.lang.dw.develop_view;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

public class VariableGraphToolingWindowPanel extends DotBasedToolingWindowPanel {


  public VariableGraphToolingWindowPanel(final Project project) {
    super(true, project);
    setupUI();
  }

  @Override
  @NotNull
  protected AnAction createAction() {
    return new RefreshASTAction();
  }

  public class RefreshASTAction extends AnAction {

    public RefreshASTAction() {
      super("Variable Graph", "Show Variable Graph from current file", AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      PsiFile currentFile = getCurrentFile();
      if (currentFile != null) {
        final String text = WeaveEditorToolingAPI.getInstance(project).scopeGraphString(currentFile);
        if (text != null) {
          updateDot(text);
        }
      }
    }

  }

}
