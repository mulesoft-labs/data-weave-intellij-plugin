package org.mule.tooling.lang.dw.debug;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.debugger.DebuggerFrame;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import org.mule.weave.v2.debugger.event.OnFrameEvent;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WeaveExecutionStack extends XExecutionStack {

  private List<XStackFrame> frames;

  protected WeaveExecutionStack(DebuggerClient client, OnFrameEvent onFrameEvent, String displayName, XDebugSession session, VirtualFile file) {
    super(displayName, AllIcons.Debugger.ThreadSuspended);
    final DebuggerFrame[] frames = onFrameEvent.frames();
    this.frames = new ArrayList<>();
    for (int i = 0; i < frames.length; i++) {
      final DebuggerFrame debuggerFrame = frames[i];
      String resourceName = debuggerFrame.startPosition().resourceName();
      VirtualFile frameFile = VirtualFileSystemUtils.resolve(NameIdentifier.apply(resourceName, Option.empty()), session.getProject());
      if (i == frames.length - 1) {
        this.frames.add(0, new WeaveStackFrame(client, onFrameEvent.startPosition(), debuggerFrame, frameFile));
      } else {
        this.frames.add(0, new WeaveStackFrame(client, debuggerFrame.startPosition(), debuggerFrame, frameFile));
      }
    }
  }


  @Nullable
  @Override
  public XStackFrame getTopFrame() {
    return frames.get(0);
  }

  @Override
  public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
    if (firstFrameIndex <= frames.size()) {
      container.addStackFrames(frames.subList(firstFrameIndex, frames.size()), true);
    } else {
      container.addStackFrames(Collections.<XStackFrame>emptyList(), true);
    }
  }
}
