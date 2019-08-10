package org.mule.tooling.runtime.debugger.runner;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.DefaultDebugEnvironment;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.tree.render.BatchEvaluator;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.mulesoft.mule.debugger.response.MuleMessageInfo;
import com.mulesoft.mule.debugger.response.ObjectFieldDefinition;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.debugger.ContextAwareDebugProcess;
import org.mule.tooling.runtime.debugger.MuleDebugProcess;
import org.mule.tooling.runtime.debugger.session.MessageReceivedListener;
import org.mule.tooling.runtime.debugger.session.MuleDebuggerSession;
import org.mule.tooling.runtime.launcher.configuration.runner.MuleRunnerState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.mule.tooling.esb.launcher.configuration.MuleConfiguration;
//import org.mule.tooling.esb.util.MuleConfigUtils;

//import org.mule.tooling.esb.debugger.ContextAwareDebugProcess;
//import org.mule.tooling.esb.debugger.MuleDebugProcess;
//import org.mule.tooling.esb.debugger.session.MessageReceivedListener;
//import org.mule.tooling.esb.debugger.session.MuleDebuggerSession;
//import org.mule.tooling.esb.debugger.mule4.ContextAwareDebugProcess;
//import org.mule.tooling.esb.debugger.mule4.MuleDebugProcess;
//import org.mule.tooling.esb.debugger.mule4.session.MessageReceivedListener;
//import org.mule.tooling.esb.debugger.mule4.session.MuleDebuggerSession;


public class MuleProgramDebugger extends GenericDebuggerRunner {

    @NonNls
    private static final String ID = "MuleESB4DebuggerRunner";
    public static final String JAVA_CONTEXT = "Java";
    public static final String MULE_CONTEXT = "Mule";

    public MuleProgramDebugger() {
        super();
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID);// && profile instanceof MuleConfiguration;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        return createContentDescriptor(state, env);
    }

    @Nullable
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            final JavaParameters parameters = ((JavaCommandLine)state).getJavaParameters();
            runCustomPatchers(parameters, environment.getExecutor(), environment.getRunProfile());
            boolean isPollConnection = true;
            RemoteConnection connection = null;
            if (state instanceof RemoteConnectionCreator) {
                connection = ((RemoteConnectionCreator)state).createRemoteConnection(environment);
                isPollConnection = ((RemoteConnectionCreator)state).isPollConnection();
            }
            if (connection == null) {
//                int transport = DebuggerSettings.getInstance().DEBUGGER_TRANSPORT;
                int transport = DebuggerSettings.SOCKET_TRANSPORT;

                connection = DebuggerManagerImpl.createDebugParameters(parameters,
                        true,
                        transport,
                        transport == DebuggerSettings.SOCKET_TRANSPORT ? String.valueOf(((MuleRunnerState)state).getPort()) : "",
                        false);
                isPollConnection = true;
            }
            return attachVirtualMachine(state, environment, connection, isPollConnection);
        } else {
            return super.createContentDescriptor(state, environment);
        }
/*
        if (state instanceof PatchedRunnableState) {
            final RemoteConnection connection = doPatch(new JavaParameters(), environment.getRunnerSettings(), true);
            return attachVirtualMachine(state, environment, connection, true);
        }
        if (state instanceof RemoteState) {
            final RemoteConnection connection = createRemoteDebugConnection((RemoteState)state, environment.getRunnerSettings());
            return attachVirtualMachine(state, environment, connection, false);
        }
*/

        //return null;
    }

    @Nullable
    protected RunContentDescriptor attachVirtualMachine(final RunProfileState state, final @NotNull ExecutionEnvironment env, RemoteConnection connection, boolean pollConnection)
            throws ExecutionException {

            return _debugMule4(state, env, connection, pollConnection);
        /*
        DefaultDebugEnvironment environment = new DefaultDebugEnvironment(env, state, connection, pollConnection);
        final DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(env.getProject()).attachVirtualMachine(environment);
        final MuleDebuggerSession muleDebuggerSession = new MuleDebuggerSession(env.getProject());
        if (debuggerSession == null) {
            return null;
        } else {
            final DebugProcessImpl debugProcess = debuggerSession.getProcess();
            if (!debugProcess.isDetached() && !debugProcess.isDetaching()) {
                if (environment.isRemote()) {
                    debugProcess.putUserData(BatchEvaluator.REMOTE_SESSION_KEY, Boolean.TRUE);
                }

                return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
                    @NotNull
                    public XDebugProcess start(@NotNull XDebugSession session) {

                        final XDebugSessionImpl sessionImpl = (XDebugSessionImpl) session;
                        final ExecutionResult executionResult = debugProcess.getExecutionResult();
                        final Map<String, XDebugProcess> context = new HashMap<>();
                        final ContextAwareDebugProcess contextAwareDebugProcess = new ContextAwareDebugProcess(session, executionResult.getProcessHandler(), context, JAVA_CONTEXT);

                        muleDebuggerSession.addMessageReceivedListener(new MessageReceivedListener() {
                            @Override
                            public void onNewMessageReceived(MuleMessageInfo muleMessageInfo) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }

                            @Override
                            public void onExceptionThrown(MuleMessageInfo muleMessageInfo, ObjectFieldDefinition exceptionThrown) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }

                            @Override
                            public void onExecutionStopped(MuleMessageInfo muleMessageInfo, List<ObjectFieldDefinition> frame, String path, String internalPosition) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }
                        });

                        debuggerSession.getContextManager().addListener((newContext, event) -> contextAwareDebugProcess.setContext(JAVA_CONTEXT));

                        //Init Java Debug Process
                        sessionImpl.addExtraActions(executionResult.getActions());
                        if (executionResult instanceof DefaultExecutionResult) {
                            sessionImpl.addRestartActions(((DefaultExecutionResult) executionResult).getRestartActions());
                            sessionImpl.addExtraStopActions(((DefaultExecutionResult) executionResult).getAdditionalStopActions());
                        }
                        final JavaDebugProcess javaDebugProcess = JavaDebugProcess.create(session, debuggerSession);

                        //Init Mule Debug Process
                        final MuleRunnerState muleRunnerState = (MuleRunnerState) state;
                        muleDebuggerSession.connectAsync(muleRunnerState.getHost(), muleRunnerState.getPort());
                        final MuleDebugProcess muleDebugProcess = new MuleDebugProcess(session, muleDebuggerSession, executionResult, null);

                        //Register All Processes
                        context.put(JAVA_CONTEXT, javaDebugProcess);
                        context.put(MULE_CONTEXT, muleDebugProcess);
                        return contextAwareDebugProcess;
                    }
                }).getRunContentDescriptor();
            } else {
                debuggerSession.dispose();
                muleDebuggerSession.disconnect();
                return null;
            }
        }*/
    }


    @Nullable
    protected RunContentDescriptor _debugMule4(final RunProfileState state, final @NotNull ExecutionEnvironment env, RemoteConnection connection, boolean pollConnection)
            throws ExecutionException {
        DefaultDebugEnvironment environment = new DefaultDebugEnvironment(env, state, connection, pollConnection);
        final DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(env.getProject()).attachVirtualMachine(environment);
        final MuleDebuggerSession muleDebuggerSession = new MuleDebuggerSession(env.getProject());
        if (debuggerSession == null) {
            return null;
        } else {
            final DebugProcessImpl debugProcess = debuggerSession.getProcess();
            if (!debugProcess.isDetached() && !debugProcess.isDetaching()) {
                if (environment.isRemote()) {
                    debugProcess.putUserData(BatchEvaluator.REMOTE_SESSION_KEY, Boolean.TRUE);
                }

                return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
                    @NotNull
                    public XDebugProcess start(@NotNull XDebugSession session) {

                        final XDebugSessionImpl sessionImpl = (XDebugSessionImpl) session;
                        final ExecutionResult executionResult = debugProcess.getExecutionResult();
                        final Map<String, XDebugProcess> context = new HashMap<>();
                        final ContextAwareDebugProcess contextAwareDebugProcess = new ContextAwareDebugProcess(session, executionResult.getProcessHandler(), context, JAVA_CONTEXT);

                        muleDebuggerSession.addMessageReceivedListener(new MessageReceivedListener() {
                            @Override
                            public void onNewMessageReceived(MuleMessageInfo muleMessageInfo) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }

                            @Override
                            public void onExceptionThrown(MuleMessageInfo muleMessageInfo, ObjectFieldDefinition exceptionThrown) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }

                            @Override
                            public void onExecutionStopped(MuleMessageInfo muleMessageInfo, List<ObjectFieldDefinition> frame, String path, String internalPosition) {
                                contextAwareDebugProcess.setContext(MULE_CONTEXT);
                            }
                        });

                        debuggerSession.getContextManager().addListener((newContext, event) -> contextAwareDebugProcess.setContext(JAVA_CONTEXT));

                        //Init Java Debug Process
                        sessionImpl.addExtraActions(executionResult.getActions());
                        if (executionResult instanceof DefaultExecutionResult) {
                            sessionImpl.addRestartActions(((DefaultExecutionResult) executionResult).getRestartActions());
                            sessionImpl.addExtraStopActions(((DefaultExecutionResult) executionResult).getAdditionalStopActions());
                        }
                        final JavaDebugProcess javaDebugProcess = JavaDebugProcess.create(session, debuggerSession);

                        //Init Mule Debug Process
                        final MuleRunnerState muleRunnerState = (MuleRunnerState) state;
                        muleDebuggerSession.connectAsync(muleRunnerState.getHost(), muleRunnerState.getPort());
                        final MuleDebugProcess muleDebugProcess = new MuleDebugProcess(session, muleDebuggerSession, executionResult, null);

                        //Register All Processes
                        context.put(JAVA_CONTEXT, javaDebugProcess);
                        context.put(MULE_CONTEXT, muleDebugProcess);
                        return contextAwareDebugProcess;
                    }
                }).getRunContentDescriptor();
            } else {
                debuggerSession.dispose();
                muleDebuggerSession.disconnect();
                return null;
            }
        }
    }

}