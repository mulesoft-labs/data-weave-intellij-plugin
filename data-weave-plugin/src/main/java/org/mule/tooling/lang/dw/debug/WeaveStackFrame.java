package org.mule.tooling.lang.dw.debug;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.debug.value.WeaveValueFactory;
import org.mule.weave.v2.debugger.DebuggerFrame;
import org.mule.weave.v2.debugger.DebuggerPosition;
import org.mule.weave.v2.debugger.DebuggerValue;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import scala.Tuple2;

public class WeaveStackFrame extends XStackFrame {

    private DebuggerClient client;
    private DebuggerPosition debuggerPosition;
    private final DebuggerFrame frame;
    private final XSourcePositionImpl xSourcePosition;
    private Project project;

    public WeaveStackFrame(DebuggerClient client, DebuggerPosition debuggerPosition, DebuggerFrame frame, VirtualFile weaveFile, Project project) {
        this.client = client;
        this.debuggerPosition = debuggerPosition;
        this.frame = frame;
        this.xSourcePosition = XSourcePositionImpl.create(weaveFile, debuggerPosition.line() - 1, debuggerPosition.column());
        this.project = project;
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        XSourcePosition position = getSourcePosition();
        if (position != null) {
            if (frame.name().isDefined()) {
                component.append( frame.name().get(), SimpleTextAttributes.GRAY_ATTRIBUTES);
            } else {
                component.append("Anonymous", SimpleTextAttributes.GRAY_ATTRIBUTES);
            }
            component.append(" [" + (position.getLine() + 1) + ":" + debuggerPosition.column() + "]", SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(" - "+ debuggerPosition.resourceName(), SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else {
            component.append(XDebuggerBundle.message("invalid.frame"), SimpleTextAttributes.ERROR_ATTRIBUTES);
        }
    }


    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        return xSourcePosition;
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new WeaveScriptEvaluator(project, client, frame.id());
    }

    @Override
    public Object getEqualityObject() {
        return WeaveStackFrame.class;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList children = new XValueChildrenList();
        final Tuple2<String, DebuggerValue>[] values = frame.values();
        for (Tuple2<String, DebuggerValue> value : values) {
            children.add(value._1, WeaveValueFactory.create(project, value._2));
        }
        node.addChildren(children, true);
    }

}

