package org.mule.tooling.bat.launcher;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveCommandLineState;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveRunnerHelper;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatTestRunnerCommandLine extends WeaveCommandLineState {
    public static final String BAT_MAIN_CLASS = "com.mulesoft.bat.EntryPoint";

    //Mule Main Class

    private final boolean isDebug;
    private BatTestConfiguration configuration;

    public BatTestRunnerCommandLine(@NotNull ExecutionEnvironment environment, BatTestConfiguration configuration) {
        super(environment);
        this.isDebug = DefaultDebugExecutor.EXECUTOR_ID.equals(environment.getExecutor().getId());
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters javaParams = new JavaParameters();
        // Use the same JDK as the project
        final Module module = this.configuration.getModule();
        javaParams.setJdk(JavaParameters.getValidJdkToRunModule(module, false));
        // All modules to use the same things
        javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
        javaParams.setMainClass(BAT_MAIN_CLASS);
        //Add default vm parameters
        WeaveRunnerHelper.setupDefaultVMParams(javaParams);
        ParametersList params = javaParams.getProgramParametersList();
        final Project project = module.getProject();
        javaParams.setWorkingDirectory(project.getBasePath());
//        params.add("-testlistener");
//        params.add("intellij");

//    if (isDebug) {
//      params.add("-debug");
//    }
        // VM Args
        final String vmArgs = this.configuration.getVmOptions();
        if (vmArgs != null) {
            javaParams.getVMParametersList().addParametersString(vmArgs);
        }
        if (!StringUtils.isBlank(configuration.getNameIdentifier())) {
            if (configuration.isBatYaml()) {
                params.add(configuration.getNameIdentifier());
            } else {
                final NameIdentifier nameIdentifier = NameIdentifier.apply(configuration.getNameIdentifier(), Option.empty());
                final VirtualFile virtualFile = VirtualFileSystemUtils.resolve(project, nameIdentifier);
                params.add(virtualFile.getPath());
            }
        }
        // All done, run it
        return javaParams;
    }
//
//    private String getYamlRunPath(Project project) {
//        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//        final List<PsiFile> files = new ArrayList<>();
//        ApplicationManager.getApplication().runReadAction(() -> {
//            final List<PsiFile> psiFiles = Arrays.asList(FilenameIndex.getFilesByName(project, configuration.getNameIdentifier(), scope));
//            files.addAll(psiFiles);
//        });
//        if (!files.isEmpty()) {
//            return files.get(0).getVirtualFile().getPath();
//        } else {
//            return project.getBaseDir().getPath();
//        }
//
//    }

//    @NotNull
//    @Override
//    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
//        ProcessHandler processHandler = startProcess();
//        RunConfiguration runConfiguration = getConfiguration();
//        TestConsoleProperties properties = new SMTRunnerConsoleProperties(runConfiguration, "WeaveTest", executor);
//        ConsoleView console = SMTestRunnerConnectionUtil.createAndAttachConsole("WeaveTest", processHandler, properties);
//        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
//    }

    public BatTestConfiguration getConfiguration() {
        return configuration;
    }

}
