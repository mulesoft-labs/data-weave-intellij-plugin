package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.debugger.event.ImplicitInputTypesEvent;
import org.mule.weave.v2.debugger.event.InferWeaveTypeEvent;

public interface InferTypeResultCallback {

    void onType(InferWeaveTypeEvent event);
}
