package org.mule.tooling.runtime.tooling;

import java.util.EventListener;

public interface ToolingRuntimeListener extends EventListener {

  void onToolingRuntimeStarted(String muleVersion);
}
