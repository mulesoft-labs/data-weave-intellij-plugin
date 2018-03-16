package org.mule.tooling.platform;

import java.util.Arrays;
import java.util.List;

public class PlatformRegion {

    private static final String US_NAME = "US";
    private static final String EU_NAME = "EU";
    private static final String EU_PREFIX = "eu1";
    private static final String MASTER_ORG_ID_EU = "e0b4a150-f59b-46d4-ad25-5d98f9deb24a";
    private static final String MASTER_ORG_ID_US = "68ef9520-24e9-4cf2-b2f5-620025690913";
    public static final PlatformRegion EU = new PlatformRegion(EU_NAME, EU_PREFIX, MASTER_ORG_ID_EU);
    public static final PlatformRegion US = new PlatformRegion(US_NAME, "", MASTER_ORG_ID_US);

    private String name;
    private String prefix;
    private String masterOrgId;

    public PlatformRegion(String name, String prefix, String masterOrgId) {
        this.name = name;
        this.prefix = prefix;
        this.masterOrgId = masterOrgId;
    }

    public static List<PlatformRegion> getRegions() {
        return Arrays.asList(US, EU);
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMasterOrgId() {
        return masterOrgId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((masterOrgId == null) ? 0 : masterOrgId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlatformRegion other = (PlatformRegion) obj;
        if (masterOrgId == null) {
            if (other.masterOrgId != null)
                return false;
        } else if (!masterOrgId.equals(other.masterOrgId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        return true;
    }

}