package org.mule.tooling.lang.dw.debug.value;

import com.intellij.openapi.project.Project;
import com.intellij.util.PlatformIcons;
import com.intellij.xdebugger.frame.*;
import org.jetbrains.annotations.NotNull;
import org.mule.weave.v2.debugger.AttributeDebuggerValue;
import org.mule.weave.v2.debugger.FieldDebuggerValue;
import org.mule.weave.v2.debugger.KeyDebuggerValue;

public class FieldWeaveValue extends XValue {

    private FieldDebuggerValue debuggerValue;
    private Project project;

    public FieldWeaveValue(Project project, FieldDebuggerValue debuggerValue) {
        this.debuggerValue = debuggerValue;
        this.project = project;
    }

    @Override
    public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace) {
        xValueNode.setPresentation(PlatformIcons.VARIABLE_ICON, "", "", true);
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList list = new XValueChildrenList();
        final KeyDebuggerValue key = debuggerValue.key();
        final AttributeDebuggerValue[] attr = key.attr();
        for (AttributeDebuggerValue attributeDebuggerValue : attr) {
            list.add("@" + attributeDebuggerValue.name(), WeaveValueFactory.create(project, attributeDebuggerValue.value()));
        }
        list.add("value", WeaveValueFactory.create(project, debuggerValue.value()));
        node.addChildren(list, false);
        super.computeChildren(node);
    }
}
