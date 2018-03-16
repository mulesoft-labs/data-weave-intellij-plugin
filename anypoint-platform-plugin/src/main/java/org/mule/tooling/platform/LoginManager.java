package org.mule.tooling.platform;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginManager implements ApplicationComponent {

    private PlatformUser currentUser = LoginStorageState.getInstance().getCurrentUser();

    private List<UserLoginListener> listeners = new ArrayList<>();

    public LoginManager() {
        listeners.add(new UserLoginListener() {
            @Override
            public void onUserLogged(PlatformUser user) {
                LoginStorageState.getInstance().setCurrentUser(user);
            }

            @Override
            public void onUserLoggedOut() {
                LoginStorageState.getInstance().setCurrentUser(null);
            }
        });
    }

    public static LoginManager getInstance() {
        return ApplicationManager.getApplication().getComponent(LoginManager.class);
    }

    public Optional<PlatformUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public Optional<PlatformUser> login() {
        LoginUI loginUI = new LoginUI();
        loginUI.show();
        currentUser = loginUI.getLoggedUser();
        if (currentUser != null) {
            for (UserLoginListener listener : listeners) {
                listener.onUserLogged(currentUser);
            }
        }
        return Optional.empty();
    }

    public void logout() {
        this.currentUser = null;
        for (UserLoginListener listener : listeners) {
            listener.onUserLoggedOut();
        }
    }

    public void addListener(UserLoginListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Anypoint Platform Login";
    }

    public interface UserLoginListener {
        void onUserLogged(PlatformUser user);

        void onUserLoggedOut();
    }
}
