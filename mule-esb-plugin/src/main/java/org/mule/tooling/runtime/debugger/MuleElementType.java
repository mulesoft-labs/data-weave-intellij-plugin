package org.mule.tooling.runtime.debugger;

import java.util.Arrays;

public enum MuleElementType {
    MESSAGE_SOURCE("abstractMessageSourceType", "inboundEndpointType"),
    MESSAGE_PROCESSOR("abstractMessageProcessorType", "outboundEndpointType"),
    EXCEPTION_STRATEGY("abstractExceptionStrategyType"),
    CONFIG("abstractExtensionType"),
    TRANSPORT_CONNECTOR("abstractConnectorType");

    private String[] validTypes;

    MuleElementType(String... validTypes) {
        this.validTypes = validTypes;
    }

    public boolean isValidType(String type) {
        return Arrays.asList(validTypes).contains(type);
    }
}
