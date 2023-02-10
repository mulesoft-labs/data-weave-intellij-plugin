package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.agent.api.event.ImplicitInputTypesEvent;

public interface ImplicitInputTypesCallback  {

    void onInputsTypesCalculated(ImplicitInputTypesEvent event);
}
