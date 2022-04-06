package org.mule.tooling.als.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.als.component.ALSLanguageService;

public class RestartALSServerAction extends AnAction {

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      ALSLanguageService.getInstance(project).restart();
      Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "ALS server restarted", "The ALS server was restarted successfully.", NotificationType.INFORMATION));
    }
  }
}
