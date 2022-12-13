package org.mule.tooling.restsdk.wizard;

public class RestSdkConfigurationModel {
    private String restSdkVersion;
    private String connectorName;
    private String apiSpec;

    public RestSdkConfigurationModel(String restSdkVersion, String connectorName, String apiSpec) {
        this.restSdkVersion = restSdkVersion;
        this.connectorName = connectorName;
        this.apiSpec = apiSpec;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public void setApiSpec(String apiSpec) {
        this.apiSpec = apiSpec;
    }

    public String getApiSpec() {
        return apiSpec;
    }

    public String getRestSdkVersion() {
        return restSdkVersion;
    }

    public void setRestSdkVersion(String restSdkVersion) {
        this.restSdkVersion = restSdkVersion;
    }
}
