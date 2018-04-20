package org.mule.tooling.lang.dw.agent;

import org.mule.weave.v2.debugger.event.ModuleResolvedEvent;

public interface ModuleLoadedCallback {

    void onModuleResolved(ModuleResolvedEvent event);
}
