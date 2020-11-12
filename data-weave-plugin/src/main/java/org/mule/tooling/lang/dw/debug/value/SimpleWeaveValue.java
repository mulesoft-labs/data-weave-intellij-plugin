package org.mule.tooling.lang.dw.debug.value;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.debugger.DebuggerPosition;
import org.mule.weave.v2.debugger.SimpleDebuggerValue;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

public class SimpleWeaveValue extends XValue {

  private final Project project;
  private SimpleDebuggerValue debuggerValue;

  public SimpleWeaveValue(Project project, SimpleDebuggerValue debuggerValue) {
    this.debuggerValue = debuggerValue;
    this.project = project;
  }

  @Override
  public void computeSourcePosition(@NotNull XNavigatable navigatable) {
    if (hasPosition()) {
      final DebuggerPosition debuggerPosition = debuggerValue.location().start();
      final String resourceName = debuggerPosition.resourceName();
      VirtualFile frameFile = VirtualFileSystemUtils.resolve(project, NameIdentifier.apply(resourceName, Option.empty()));
      if (frameFile != null) {
        navigatable.setSourcePosition(XSourcePositionImpl.create(frameFile, debuggerPosition.line(), debuggerPosition.column()));
      }
    }
  }

  public boolean hasPosition() {
    return debuggerValue.location().start().line() > 0;
  }

  @Override
  public @NotNull ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
    return  ThreeState.YES;
  }

  @Override
  public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace) {
    //Lets make sure if it is null to be a string
    final String presentationValue = String.valueOf(debuggerValue.value());
    xValueNode.setPresentation(PlatformIcons.VARIABLE_ICON, debuggerValue.typeName(), presentationValue, false);
  }

}
