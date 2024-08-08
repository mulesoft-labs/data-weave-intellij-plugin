package org.mule.tooling.lang.dw.develop_view;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.service.WeaveToolingService;

public class DependencyGraphToolingWindowPanel extends DotBasedToolingWindowPanel {


    public DependencyGraphToolingWindowPanel(final Project project) {
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
            super("Dependency Graph", "Show dependencies from the modules.", AllIcons.Actions.Refresh);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            final String text = WeaveToolingService.getInstance(project).dependencyGraph();
            if (text != null) {
                updateDot(text);
            }
        }

    }

}
