package org.mule.tooling.lang.dw.debug;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.debug.breakpoint.WeaveBreakpointHandler;
import org.mule.tooling.lang.dw.debug.breakpoint.WeaveDebuggerEditorsProvider;
import org.mule.weave.v2.debugger.client.DebuggerClient;

public class WeaveDebugProcess extends XDebugProcess {
    private final DebuggerClient weaveDebuggerClient;
    private final ProcessHandler processHandler;
    private final ExecutionConsole executionConsole;
    private final WeaveBreakpointHandler breakpointHandler;


    protected WeaveDebugProcess(@NotNull XDebugSession session, DebuggerClient debuggerClient, ExecutionResult result) {
        super(session);
        this.weaveDebuggerClient = debuggerClient;
        this.processHandler = result.getProcessHandler();
        this.executionConsole = result.getExecutionConsole();
        this.breakpointHandler = new WeaveBreakpointHandler(session.getProject(), debuggerClient);
    }

    @NotNull
    @Override
    public ExecutionConsole createConsole() {
        return executionConsole;
    }

    @Override
    protected ProcessHandler doGetProcessHandler() {
        return processHandler;
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return new WeaveDebuggerEditorsProvider();
    }


    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        this.weaveDebuggerClient.nextStep();
    }


    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        this.weaveDebuggerClient.stepInto();
    }

    @NotNull
    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        final XBreakpointHandler<?>[] breakpointHandlers = super.getBreakpointHandlers();
        return ArrayUtil.append(breakpointHandlers, breakpointHandler);
    }

    @Override
    public void sessionInitialized() {
        super.sessionInitialized();
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        weaveDebuggerClient.nextStep();
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new WeaveScriptEvaluator(getSession().getProject(), weaveDebuggerClient);
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        weaveDebuggerClient.resume();
    }

    @Override
    public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
        //TODO
    }

    @Override
    public void stop() {
        weaveDebuggerClient.disconnect();
    }
}
