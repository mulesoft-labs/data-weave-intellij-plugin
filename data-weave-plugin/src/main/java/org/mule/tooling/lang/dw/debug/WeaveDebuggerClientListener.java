package org.mule.tooling.lang.dw.debug;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.debugger.DebuggerPosition;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import org.mule.weave.v2.debugger.client.DebuggerClientListener;
import org.mule.weave.v2.debugger.event.BreakpointAddedEvent;
import org.mule.weave.v2.debugger.event.BreakpointRemovedEvent;
import org.mule.weave.v2.debugger.event.OnFrameEvent;
import org.mule.weave.v2.debugger.event.ScriptResultEvent;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

public class WeaveDebuggerClientListener implements DebuggerClientListener {

  private XDebugSession session;


  public WeaveDebuggerClientListener(XDebugSession session) {
    this.session = session;

  }

  @Override
  public void onFrame(DebuggerClient client, OnFrameEvent frame) {
    final DebuggerPosition debuggerPosition = frame.startPosition();
    final String resourceName = debuggerPosition.resourceName();
    final VirtualFile file = VirtualFileSystemUtils.resolve(session.getProject(), NameIdentifier.apply(resourceName, Option.empty()));
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
