package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.agent.api.event.AvailableModulesEvent;

public interface AvailableModulesCallback {

    void onAvailableModules(AvailableModulesEvent event);
}
