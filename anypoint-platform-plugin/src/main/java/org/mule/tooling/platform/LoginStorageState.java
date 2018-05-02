package org.mule.tooling.platform;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(name = "LoginStorageState", storages = @Storage(file = "Anypoint/LoginStorageState.xml"))
public class LoginStorageState implements PersistentStateComponent<LoginStorageState> {

    //TODO for the future we may need to support multiple users
    private PlatformUser currentUser;

    public PlatformUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(PlatformUser currentUser) {
        this.currentUser = currentUser;
    }

    public static LoginStorageState getInstance() {
        return ServiceManager.getService(LoginStorageState.class);
    }

    @Nullable
    @Override
    public LoginStorageState getState() {
        return this;
    }

    @Override
    public void loadState(LoginStorageState loginStorageState) {
        XmlSerializerUtil.copyBean(loginStorageState, this);
    }
}
