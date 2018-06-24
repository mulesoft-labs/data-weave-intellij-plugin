package org.mule.tooling.runtime.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "AnypointRuntime", storages = @Storage(file = "Anypoint/Runtime.xml"))
public class MuleRuntimeSettingsState implements PersistentStateComponent<MuleRuntimeSettingsState> {


  private String defaultRuntimeVersion = "4.1.3-SNAPSHOT";

  private List<MavenRepository> additionalRepositories = new ArrayList<>();

  public static MuleRuntimeSettingsState getInstance() {
    return ServiceManager.getService(MuleRuntimeSettingsState.class);
  }

  public String getDefaultRuntimeVersion() {
    return defaultRuntimeVersion;
  }

  public void setDefaultRuntimeVersion(String defaultRuntimeVersion) {
    this.defaultRuntimeVersion = defaultRuntimeVersion;
  }

  public List<MavenRepository> getAdditionalRepositories() {
    return additionalRepositories;
  }

  public void setAdditionalRepositories(List<MavenRepository> additionalRepositories) {
    this.additionalRepositories = additionalRepositories;
  }

  @Nullable
  @Override
  public MuleRuntimeSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(MuleRuntimeSettingsState muleRuntimeSettingsState) {
    XmlSerializerUtil.copyBean(muleRuntimeSettingsState, this);
  }


}
