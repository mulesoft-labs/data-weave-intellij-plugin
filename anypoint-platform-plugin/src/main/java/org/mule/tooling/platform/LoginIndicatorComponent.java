package org.mule.tooling.platform;

import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class LoginIndicatorComponent implements StatusBarWidget, StatusBarWidget.IconPresentation {
    private StatusBar myStatusBar;

    private Icon myCurrentIcon = PlatformsIcons.OFFLINE;
    private String myToolTipText;


    public LoginIndicatorComponent() {
        clear();
    }

    public void clear() {
        update(PlatformsIcons.OFFLINE, "Click to login");
    }

    public void changeState(@NotNull final String toolTipText) {
        update(PlatformsIcons.ONLINE, toolTipText);
    }

    private void update(@NotNull final Icon icon, @Nullable final String toolTipText) {
        myCurrentIcon = icon;
        myToolTipText = toolTipText;
        if (myStatusBar != null) myStatusBar.updateWidget(ID());

    }

    @NotNull
    public Icon getIcon() {
        return myCurrentIcon;
    }

    public String getTooltipText() {
        return myToolTipText;
    }

    public Consumer<MouseEvent> getClickConsumer() {
        return mouseEvent -> {
            if (myStatusBar != null) {
                if (!LoginManager.getInstance().getCurrentUser().isPresent()) {
                    LoginManager.getInstance().login();
                } else {
                    LoginManager.getInstance().logout();
                }
            }
        };
    }

    @NotNull
    public String ID() {
        return "AnypointPlatformLogin";
    }

    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }

    public void install(@NotNull StatusBar statusBar) {
        myStatusBar = statusBar;
    }

    public void dispose() {
        myStatusBar = null;
    }
}
