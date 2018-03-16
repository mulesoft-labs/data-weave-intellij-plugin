package org.mule.tooling.platform.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.platform.PlatformRegion;
import org.mule.tooling.platform.PlatformUrls;

@State(name = "AnypointPlatform", storages = @Storage(id = "AnypointPlatform", file = "Anypoint/Settings.xml"))
public class PlatformSettingsState implements PersistentStateComponent<PlatformSettingsState> {

    private Boolean onPremise = false;
    private String customUrl = PlatformUrls.BASE_PLATFORM_URL;
    private PlatformRegion region = PlatformRegion.US;

    public static PlatformSettingsState getInstance() {
        return ServiceManager.getService(PlatformSettingsState.class);
    }


    public Boolean isOnPremise() {
        return onPremise;
    }

    public void setOnPremise(Boolean onPremise) {
        this.onPremise = onPremise;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public PlatformRegion getRegion() {
        return region;
    }

    public void setRegion(PlatformRegion region) {
        this.region = region;
    }

    @Nullable
    @Override
    public PlatformSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(PlatformSettingsState platformSettingsState) {
        XmlSerializerUtil.copyBean(platformSettingsState, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformSettingsState that = (PlatformSettingsState) o;

        if (onPremise != null ? !onPremise.equals(that.onPremise) : that.onPremise != null) return false;
        if (customUrl != null ? !customUrl.equals(that.customUrl) : that.customUrl != null) return false;
        return region != null ? region.equals(that.region) : that.region == null;
    }

    @Override
    public int hashCode() {
        int result = onPremise != null ? onPremise.hashCode() : 0;
        result = 31 * result + (customUrl != null ? customUrl.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }
}
