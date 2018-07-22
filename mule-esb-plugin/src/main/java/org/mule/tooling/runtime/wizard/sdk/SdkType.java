package org.mule.tooling.runtime.wizard.sdk;

public enum SdkType {
    JAVA("Java Module"), XML("XML Module");

    private String displayName;

    SdkType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
