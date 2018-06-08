/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tooling.runtime.tooling;

public class MuleAgentConfiguration {

    private int agentPort;
    private long startTimeout;
    private long startPollInterval;
    private long startPollDelay;

    private String protocol;

    public MuleAgentConfiguration(
            String protocol,
            int agentPort,
            long startTimeout,
            long startPollInterval,
            long startPollDelay
    ) {
        this.agentPort = agentPort;
        this.startTimeout = startTimeout;
        this.startPollInterval = startPollInterval;
        this.startPollDelay = startPollDelay;
        this.protocol = protocol;
    }

    public long getStartTimeout() {
        return startTimeout;
    }

    public long getStartPollInterval() {
        return startPollInterval;
    }

    public long getStartPollDelay() {
        return startPollDelay;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getAgentPort() {
        return agentPort;
    }
}
