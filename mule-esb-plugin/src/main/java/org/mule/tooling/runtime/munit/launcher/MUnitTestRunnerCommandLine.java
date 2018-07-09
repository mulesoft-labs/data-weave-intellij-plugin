package org.mule.tooling.runtime.munit.launcher;

import org.jetbrains.annotations.NotNull;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.runners.ExecutionEnvironment;

public class MUnitTestRunnerCommandLine extends CommandLineState {

  protected MUnitTestRunnerCommandLine(ExecutionEnvironment environment) {
    super(environment);
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    ProcessHandlerFactory processHandlerFactory = ProcessHandlerFactory.getInstance();
    GeneralCommandLine commandLine = new GeneralCommandLine("mvn clean test");
    commandLine
        .setWorkDirectory("/Users/diegostrubolini/Documents/git/munit-git/munit-runtime-antman/munit-tests/isolated/extensions/os-connector-suite");
    return processHandlerFactory.createProcessHandler(commandLine);
  }
}
