package org.mule.tooling.lang.dw.debug;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import org.mule.weave.v2.debugger.client.DebuggerClientListener;
import org.mule.weave.v2.debugger.event.BreakpointAddedEvent;
import org.mule.weave.v2.debugger.event.BreakpointRemovedEvent;
import org.mule.weave.v2.debugger.event.OnFrameEvent;
import org.mule.weave.v2.debugger.event.ScriptResultEvent;

public class WeaveDebuggerClientListener implements DebuggerClientListener {

  private XDebugSession session;
  private final VirtualFile file;

  public WeaveDebuggerClientListener(XDebugSession session, VirtualFile file) {
    this.session = session;
    this.file = file;
  }

  @Override
  public void onFrame(DebuggerClient client, OnFrameEvent frame) {
    session.positionReached(new WeaveSuspendContext(client, frame, session, file));
  }


  @Override
  public void onBreakpointAdded(BreakpointAddedEvent bae) {

  }

  @Override
  public void onBreakpointCleaned() {

  }

  @Override
  public void onBreakpointRemoved(BreakpointRemovedEvent bre) {

  }

  @Override
  public void onScriptEvaluated(DebuggerClient client, ScriptResultEvent sr) {

  }
}
