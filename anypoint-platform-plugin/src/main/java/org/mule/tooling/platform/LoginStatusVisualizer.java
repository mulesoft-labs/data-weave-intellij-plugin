package org.mule.tooling.platform;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

public class LoginStatusVisualizer extends AbstractProjectComponent {

    protected LoginStatusVisualizer(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void projectOpened() {
        ApplicationManager.getApplication().invokeLater(() -> {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
            if (statusBar != null) {
                LoginIndicatorComponent statusBarWidget = new LoginIndicatorComponent();
                LoginManager loginManager = LoginManager.getInstance();
                if (loginManager.getCurrentUser().isPresent()) {
                    statusBarWidget.changeState("Click to logout.");
                }
                loginManager.addListener(new LoginManager.UserLoginListener() {
                    @Override
                    public void onUserLogged(PlatformUser user) {
                        statusBarWidget.changeState("Click to logout.");
                    }

                    @Override
                    public void onUserLoggedOut() {
                        statusBarWidget.clear();
                    }
                });
                statusBar.addWidget(statusBarWidget, myProject);
            }
        }, myProject.getDisposed());
    }
}


