package org.mule.tooling.lang.dw.agent;

import org.mule.weave.v2.debugger.event.ImplicitInputTypesEvent;

public interface ImplicitInputTypesCallback  {

    void onInputsTypesCalculated(ImplicitInputTypesEvent event);
}
