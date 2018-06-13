package org.mule.tooling.lang.dw.debug;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.WeaveBasedConfiguration;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveCommandLineState;
import org.mule.weave.v2.debugger.client.DebuggerClient;
import org.mule.weave.v2.debugger.client.tcp.TcpClientProtocol;

public class WeaveDebuggerRunner extends DefaultProgramRunner {

  @NonNls
  private static final String ID = "WeaveDebuggerRunner";

  public WeaveDebuggerRunner() {
    super();
  }

  @NotNull
  @Override
  public String getRunnerId() {
    return ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof WeaveBasedConfiguration;
  }

  @Override
  protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    FileDocumentManager.getInstance().saveAllDocuments();
    return attachVirtualMachine(state, env);
  }

  @Nullable
  protected RunContentDescriptor attachVirtualMachine(final RunProfileState state, final @NotNull ExecutionEnvironment env)
          throws ExecutionException {

    return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
      @NotNull
      public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
        final WeaveCommandLineState weaveRunnerCommandLine = (WeaveCommandLineState) state;
        final VirtualFile fileByRelativePath = weaveRunnerCommandLine.getWeaveFile(weaveRunnerCommandLine.getEnvironment().getProject());

        final DebuggerClient localhost = new DebuggerClient(new WeaveDebuggerClientListener(session, fileByRelativePath), new TcpClientProtocol("localhost", 6565));
        final ExecutionResult result = state.execute(env.getExecutor(), WeaveDebuggerRunner.this);
        new DebuggerConnector(localhost).start();
        return new WeaveDebugProcess(session, localhost, result);
      }
    }).getRunContentDescriptor();

  }

  private static class DebuggerConnector extends Thread {
    private final DebuggerClient localhost;

    public DebuggerConnector(DebuggerClient localhost) {
      this.localhost = localhost;
    }

    @Override
    public void run() {
      int i = 0;
      while (i < 100) {
        try {
          System.out.println("Trying to connect " + i);
          localhost.connect();
          System.out.println("Weave connected");
          return;
        } catch (Exception e) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e1) {
            return;
          }
          i++;
        }
      }
    }
  }
}