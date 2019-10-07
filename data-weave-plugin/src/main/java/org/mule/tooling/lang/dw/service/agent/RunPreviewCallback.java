package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

public interface RunPreviewCallback {

    void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result, long duration);

    void onPreviewFailed(PreviewExecutedFailedEvent message);
}
