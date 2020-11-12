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
import org.mule.weave.v2.debugger.FieldDebuggerValue;
import org.mule.weave.v2.debugger.ObjectDebuggerValue;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

public class ObjectWeaveValue extends XValue {

    private ObjectDebuggerValue debuggerValue;
    private Project project;

    public ObjectWeaveValue(Project project, ObjectDebuggerValue debuggerValue) {
        this.debuggerValue = debuggerValue;
        this.project = project;
    }

    @Override
    public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace) {
        xValueNode.setPresentation(PlatformIcons.VARIABLE_ICON, "Object", "length " + debuggerValue.fields().length + "'", true);
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
        final FieldDebuggerValue[] innerElements = debuggerValue.fields();
        for (FieldDebuggerValue innerElement : innerElements) {
            final XValue value = innerElement.key().attr().length > 0 ?
                    WeaveValueFactory.create(project, innerElement) : WeaveValueFactory.create(project, innerElement.value());
            list.add(innerElement.key().name(), value);
        }
        node.addChildren(list, false);
        super.computeChildren(node);
    }
}
