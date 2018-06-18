package org.mule.tooling.runtime.tooling;

import com.intellij.util.messages.Topic;

public class ToolingRuntimeTopics {

  public static final Topic<ToolingRuntimeListener> TOOLING_STARTED = new Topic<>("tooling runtime listener", ToolingRuntimeListener.class);
}
