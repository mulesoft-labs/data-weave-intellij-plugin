package org.mule.tooling.lang.dw.service.agent;

import org.mule.weave.v2.agent.api.event.PreviewExecutedFailedEvent;
import org.mule.weave.v2.agent.api.event.PreviewExecutedSuccessfulEvent;

public interface RunPreviewCallback {

    void onPreviewSuccessful(PreviewExecutedSuccessfulEvent result, long duration);

    void onPreviewFailed(PreviewExecutedFailedEvent message);
}
