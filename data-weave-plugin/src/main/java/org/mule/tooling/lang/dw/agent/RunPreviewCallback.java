package org.mule.tooling.lang.dw.agent;

import org.mule.weave.v2.debugger.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

public interface RunPreviewCallback {

    void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result);

    void onPreviewFailed(PreviewExecutedFailedEvent message);
}
