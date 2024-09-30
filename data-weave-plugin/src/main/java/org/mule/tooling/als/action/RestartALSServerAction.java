package org.mule.tooling.als.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.als.component.ALSLanguageService;
import org.mule.tooling.commons.AnypointNotification;

public class RestartALSServerAction extends AnAction {

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      ALSLanguageService.getInstance(project).restart();
      Notifications.Bus.notify(new Notification(AnypointNotification.ANYPOINT_NOTIFICATION, "ALS server restarted", "The ALS server was restarted successfully.", NotificationType.INFORMATION));
    }
  }
}
