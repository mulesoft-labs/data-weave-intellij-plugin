package org.mule.tooling.lang.dw.debug.value;

import com.intellij.xdebugger.frame.XValue;
import org.mule.weave.v2.debugger.ArrayDebuggerValue;
import org.mule.weave.v2.debugger.DebuggerFunction;
import org.mule.weave.v2.debugger.DebuggerValue;
import org.mule.weave.v2.debugger.FieldDebuggerValue;
import org.mule.weave.v2.debugger.ObjectDebuggerValue;
import org.mule.weave.v2.debugger.OperatorDebuggerValue;
import org.mule.weave.v2.debugger.SimpleDebuggerValue;


public class WeaveValueFactory {


  public static XValue create(DebuggerValue value) {
    if (value instanceof ArrayDebuggerValue) {
      return new ArrayWeaveValue((ArrayDebuggerValue) value);
    } else if (value instanceof ObjectDebuggerValue) {
      return new ObjectWeaveValue((ObjectDebuggerValue) value);
    } else if (value instanceof FieldDebuggerValue) {
      return new FieldWeaveValue((FieldDebuggerValue) value);
    } else if (value instanceof DebuggerFunction) {
      return new FunctionWeaveValue((DebuggerFunction) value);
    } else if (value instanceof OperatorDebuggerValue) {
      return new OperatorWeaveValue((OperatorDebuggerValue) value);
    } else if (value instanceof SimpleDebuggerValue) {
      return new SimpleWeaveValue((SimpleDebuggerValue) value);
    }

    throw new RuntimeException("Debugger value not supported ");
  }
}
