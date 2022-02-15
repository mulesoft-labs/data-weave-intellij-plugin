package org.mule.tooling.als.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "DialectsRegistry", storages = @Storage("dialectsRegistry.xml"))
public class DialectsRegistry implements PersistentStateComponent<DialectsRegistry.DialectRegistryState> {

  public static DialectsRegistry getInstance() {
    return ServiceManager.getService(DialectsRegistry.class);
  }

  private DialectRegistryState dialectsRegistry = new DialectRegistryState();

  public List<DialectLocation> getDialectsRegistry() {
    return dialectsRegistry.getDialectsRegistry();
  }

  public void setDialectsRegistry(List<DialectLocation> dialectsRegistry) {
    this.dialectsRegistry.setDialectsRegistry( new ArrayList<>(dialectsRegistry));
    DialectAddedNotifier dialectAddedNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(DialectAddedNotifier.CHANGE_ACTION_TOPIC);
    dialectAddedNotifier.dialectAdded(this.dialectsRegistry.getDialectsRegistry());
  }

  @Override
  public @Nullable DialectRegistryState getState() {
    return dialectsRegistry;
  }

  @Override
  public void loadState(@NotNull DialectRegistryState state) {
    XmlSerializerUtil.copyBean(state, this.dialectsRegistry);
  }

  public static class DialectRegistryState {
    private List<DialectLocation> dialectsRegistry = new ArrayList<>();

    public DialectRegistryState(List<DialectLocation> dialectsRegistry) {
      this.dialectsRegistry = dialectsRegistry;
    }

    public DialectRegistryState() {
    }

    public List<DialectLocation> getDialectsRegistry() {
      return dialectsRegistry;
    }

    public void setDialectsRegistry(List<DialectLocation> dialectsRegistry) {
      this.dialectsRegistry = dialectsRegistry;
    }
  }

  public static class DialectLocation {
    String name;
    String dialectFilePath;

    public DialectLocation(String name, String dialectFilePath) {
      this.name = name;
      this.dialectFilePath = dialectFilePath;
    }

    public DialectLocation() {
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setDialectFilePath(String dialectFilePath) {
      this.dialectFilePath = dialectFilePath;
    }

    public String getName() {
      return name;
    }

    public String getDialectFilePath() {
      return dialectFilePath;
    }
  }

  public interface DialectAddedNotifier {

    Topic<DialectAddedNotifier> CHANGE_ACTION_TOPIC = Topic.create("dialectAdded", DialectAddedNotifier.class);

    void dialectAdded(List<DialectLocation> context);

  }
}
