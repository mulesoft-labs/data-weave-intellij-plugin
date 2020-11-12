package org.mule.tooling.lang.dw.debug.value;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.debugger.ArrayDebuggerValue;
import org.mule.weave.v2.debugger.DebuggerPosition;
import org.mule.weave.v2.debugger.DebuggerValue;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

public class ArrayWeaveValue extends XValue {

    private ArrayDebuggerValue debuggerValue;
    private Project project;

    public ArrayWeaveValue(Project project, ArrayDebuggerValue debuggerValue) {
        this.debuggerValue = debuggerValue;
        this.project = project;
    }

    @Override
    public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace) {
        xValueNode.setPresentation(PlatformIcons.VARIABLE_ICON, "Array", "length : " + debuggerValue.values().length, true);
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
        return hasPosition() ? ThreeState.YES : ThreeState.NO;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList list = new XValueChildrenList();
        final DebuggerValue[] innerElements = debuggerValue.values();
        int i = 0;
        for (DebuggerValue innerElement : innerElements) {
            final XValue value = WeaveValueFactory.create(project, innerElement);
            list.add("[" + i + "]", value);
            i++;
        }
        node.addChildren(list, false);
        super.computeChildren(node);
    }
}
