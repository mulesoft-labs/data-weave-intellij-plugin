package org.mule.tooling.runtime.wizard.sdk;

public class SdkProject {

    private String name;
    private String version;
    private SdkType type;
    private boolean createTests;
    private boolean createMTFTests;
    private boolean createSource;

    public SdkProject(String name, String version, SdkType type, boolean createTests, boolean createMTFTests, boolean createSource) {
        this.name = name;
        this.version = version;
        this.type = type;
        this.createTests = createTests;
        this.createMTFTests = createMTFTests;
        this.createSource = createSource;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public SdkType getType() {
        return type;
    }

    public boolean isCreateTests() {
        return createTests;
    }

    public boolean isCreateMTFTests() {
        return createMTFTests;
    }

    public boolean isCreateSource() {
        return createSource;
    }
}