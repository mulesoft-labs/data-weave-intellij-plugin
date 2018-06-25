package org.mule.tooling.runtime.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MuleSdkManager implements ApplicationComponent {

    public static MuleSdkManager getInstance() {
        return ApplicationManager.getApplication().getComponent(MuleSdkManager.class);
    }

    private Map<String, MuleSdk> runtimes = new HashMap<>();

    @Override
    public void initComponent() {
        File muleDistroDirectory = getMuleDistroDirectory();
        File[] distros = muleDistroDirectory.listFiles();
        if (distros != null) {
            for (File distro : distros) {
                MuleSdk.create(distro.getAbsolutePath()).ifPresent(muleSdk -> runtimes.put(muleSdk.getVersion(), muleSdk));
            }
        }
    }

  @Nullable
  public MuleSdk getSdkByVersion(String muleVersion) {
    MuleSdk muleSdk = runtimes.get(muleVersion);
    if (muleSdk == null) {
      MuleSdk sdk = MuleSdkManagerStore.getInstance().findSdk(muleVersion);
      if (sdk == null) {
        //TODO
        return null;
      } else {
        return sdk;
      }
    } else {
      return muleSdk;
    }
  }

    public File getMuleDistroDirectory() {
      return MuleDirectoriesUtils.getMuleRuntimesHomeDirectory();
    }
}
