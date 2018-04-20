package org.mule.tooling.lang.dw.agent;

import org.mule.weave.v2.debugger.event.DataFormatsDefinitionsEvent;

public interface DataFormatsDefinitionCallback {

    void onDataFormatsLoaded(DataFormatsDefinitionsEvent event);
}
