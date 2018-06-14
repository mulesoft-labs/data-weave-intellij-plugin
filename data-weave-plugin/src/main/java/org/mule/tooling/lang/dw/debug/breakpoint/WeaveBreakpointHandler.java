package org.mule.tooling.lang.dw.debug.breakpoint;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.debugger.WeaveBreakpoint;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

public class WeaveBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<XBreakpointProperties>> {
  private Project project;
  private final DebuggerClient debuggerClient;

  public WeaveBreakpointHandler(Project project, DebuggerClient debuggerClient) {
    super(WeaveBreakpointType.class);
    this.project = project;
    this.debuggerClient = debuggerClient;
  }

  @Override
  public void registerBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> lineBreakpoint) {
    debuggerClient.addBreakpoint(toWeaveBreakpoint(lineBreakpoint));
  }

  @NotNull
  private WeaveBreakpoint toWeaveBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> lineBreakpoint) {
    String fileUrl = lineBreakpoint.getPresentableFilePath();
    VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(fileUrl);
    NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, fileByPath);
    return new WeaveBreakpoint(lineBreakpoint.getLine() + 1, nameIdentifier.name(), -1, getExpression(lineBreakpoint));
  }

  @Nullable
  private String getExpression(@NotNull XLineBreakpoint<XBreakpointProperties> lineBreakpoint) {
    final XExpression conditionExpression = lineBreakpoint.getConditionExpression();
    return conditionExpression != null ? conditionExpression.getExpression() : null;
  }

  @Override
  public void unregisterBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> lineBreakpoint, boolean temporary) {
    debuggerClient.removeBreakpoint(toWeaveBreakpoint(lineBreakpoint));
  }
}
