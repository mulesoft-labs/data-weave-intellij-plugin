package org.mule.tooling.restsdk.wizzard;

public class RestSdkConfigurationModel {
    private String restSdkVersion;
    private String connectorName;
    private ApiKind apiKind;

    public RestSdkConfigurationModel(String restSdkVersion, String connectorName, ApiKind apiKind) {
        this.restSdkVersion = restSdkVersion;
        this.apiKind = apiKind;
        this.connectorName = connectorName;
    }


    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public ApiKind getApiKind() {
        return apiKind;
    }

    public void setApiKind(ApiKind apiKind) {
        this.apiKind = apiKind;
    }

    public String getRestSdkVersion() {
        return restSdkVersion;
    }

    public void setRestSdkVersion(String restSdkVersion) {
        this.restSdkVersion = restSdkVersion;
    }
}
