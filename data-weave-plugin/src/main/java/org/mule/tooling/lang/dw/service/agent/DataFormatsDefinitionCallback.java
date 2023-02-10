package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.agent.api.event.DataFormatsDefinitionsEvent;

public interface DataFormatsDefinitionCallback {

    void onDataFormatsLoaded(DataFormatsDefinitionsEvent event);
}
