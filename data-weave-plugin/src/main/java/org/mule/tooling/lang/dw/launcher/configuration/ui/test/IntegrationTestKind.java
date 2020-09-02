package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

public enum IntegrationTestKind {

    MAPPING(true, false),
    MODULE(false, true),
    ALL(true, true);

    private boolean shouldRunDWIT;
    private boolean shouldRunDWMIT;

    IntegrationTestKind(boolean shouldRunDWIT, boolean shouldRunDWMIT) {
        this.shouldRunDWIT = shouldRunDWIT;
        this.shouldRunDWMIT = shouldRunDWMIT;
    }

    public boolean shouldRunDWIT() {
        return shouldRunDWIT;
    }

    public boolean shouldRunDWMIT() {
        return shouldRunDWMIT;
    }
}
