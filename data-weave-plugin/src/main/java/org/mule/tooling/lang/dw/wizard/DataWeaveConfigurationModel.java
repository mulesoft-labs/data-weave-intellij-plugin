package org.mule.tooling.lang.dw.wizard;

public class DataWeaveConfigurationModel {

    private String weaveVersion ;
    private String weaveMavenVersion ;
    private String wtfVersion;
    private String orgId;

    public DataWeaveConfigurationModel(String weaveVersion, String weaveMavenVersion, String wtfVersion) {
        this.weaveVersion = weaveVersion;
        this.weaveMavenVersion = weaveMavenVersion;
        this.wtfVersion = wtfVersion;
        orgId = "";
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getWeaveVersion() {
        return weaveVersion;
    }

    public void setWeaveVersion(String weaveVersion) {
        this.weaveVersion = weaveVersion;
    }

    public String getWeaveMavenVersion() {
        return weaveMavenVersion;
    }

    public void setWeaveMavenVersion(String weaveMavenVersion) {
        this.weaveMavenVersion = weaveMavenVersion;
    }

    public String getWtfVersion() {
        return wtfVersion;
    }

    public void setWtfVersion(String wtfVersion) {
        this.wtfVersion = wtfVersion;
    }
}
