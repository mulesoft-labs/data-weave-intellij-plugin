package org.mule.tooling.lang.dw.debug.value;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.frame.XValue;
import org.mule.weave.v2.debugger.*;


public class WeaveValueFactory {

    public static XValue create(Project project, DebuggerValue value) {
        if (value instanceof ArrayDebuggerValue) {
            return new ArrayWeaveValue(project, (ArrayDebuggerValue) value);
        } else if (value instanceof ObjectDebuggerValue) {
            return new ObjectWeaveValue(project, (ObjectDebuggerValue) value);
        } else if (value instanceof FieldDebuggerValue) {
            return new FieldWeaveValue(project, (FieldDebuggerValue) value);
        } else if (value instanceof DebuggerFunction) {
            return new FunctionWeaveValue(project, (DebuggerFunction) value);
        } else if (value instanceof SimpleDebuggerValue) {
            return new SimpleWeaveValue(project, (SimpleDebuggerValue) value);
        }
        throw new RuntimeException("Debugger value not supported ");
    }
}
