package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.agent.api.event.InferWeaveTypeEvent;

public interface InferTypeResultCallback {

    void onType(InferWeaveTypeEvent event);
}
