package org.mule.tooling.runtime.launcher.configuration.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
//import org.codehaus.plexus.util.FileUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.runtime.launcher.configuration.MuleConfiguration;
import org.mule.tooling.runtime.launcher.configuration.archive.MuleAppHandler;
import org.mule.tooling.runtime.launcher.configuration.archive.MuleAppManager;
import org.mule.tooling.runtime.launcher.configuration.ui.MuleRunnerEditor;
import org.mule.tooling.runtime.sdk.DefaultMuleClassPathConfig;
import org.mule.tooling.runtime.tooling.MuleRuntimeServerManager;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MuleRunnerCommandLineState extends JavaCommandLineState implements MuleRunnerState {
    private static final Logger logger = Logger.getInstance(MuleRunnerCommandLineState.class);

    //Mule Main Class
    public static final String MAIN_CLASS = "org.mule.runtime.module.launcher.MuleContainer";

    private MuleConfiguration model;

    private final boolean isDebug;
    private final MuleBaseDirectory muleBaseDirectory;

    public MuleRunnerCommandLineState(@NotNull ExecutionEnvironment environment, @NotNull MuleConfiguration model) {
        super(environment);
        this.model = model;
        this.isDebug = DefaultDebugExecutor.EXECUTOR_ID.equals(environment.getExecutor().getId());
        this.muleBaseDirectory =
                model.isDeployInContainer() ?
                        MuleBaseDirectory.sameMuleBase(model.getMuleHome()):
                        MuleBaseDirectory.newFrom(model.getMuleHome(),model.getProject().getName(), MuleRuntimeServerManager.getMuleVersionOf(this.model.getProject()));
    }

    @Override
    public JavaParameters createJavaParameters() {
        JavaParameters javaParams = new JavaParameters();
        // Use the same JDK as the project
        Project project = this.model.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        // All modules to use the same things
        final String muleHome = model.getMuleHome();
        final DefaultMuleClassPathConfig muleClassPath = new DefaultMuleClassPathConfig(new File(muleHome), new File(muleHome));
        final List<File> urLs = muleClassPath.getURLs();
        for (File jar : urLs) {
            javaParams.getClassPath().add(jar);
        }
        //EE license location
        javaParams.getClassPath().add(muleHome + "/conf");

        //Mule main class
        javaParams.setMainClass(MAIN_CLASS);

        //Add default vm parameters
        javaParams.getVMParametersList().add("-Dmule.home=" + muleHome);
        javaParams.getVMParametersList().add("-Dmule.base=" + muleBaseDirectory.getMuleBase().getAbsolutePath());
        javaParams.getVMParametersList().add("-Dmule.testingMode=true");
        javaParams.getVMParametersList().add("-Djava.net.preferIPv4Stack=TRUE ");
        javaParams.getVMParametersList().add("-Dmvel2.disable.jit=TRUE");
        javaParams.getVMParametersList().add("-Dorg.glassfish.grizzly.nio.transport.TCPNIOTransport.max-receive-buffer-size=1048576");
        javaParams.getVMParametersList().add("-Dorg.glassfish.grizzly.nio.transport.TCPNIOTransport.max-send-buffer-size=1048576");
        javaParams.getVMParametersList().add("-Djava.endorsed.dirs=" + muleHome + "/lib/endorsed ");
        //TODO - is this required? There's no jul anywhere in the cp and there's an error java.lang.ClassNotFoundException: org.apache.logging.log4j.jul.LogManager
        //javaParams.getVMParametersList().add("-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager");
        javaParams.getVMParametersList().add("-Dmule.forceConsoleLog=true");

        if (isDebug) {
//            javaParams.getVMParametersList().add("-Dmule.debug.enable=true");
//            javaParams.getVMParametersList().add("-Dmule.debug.suspend=false");
//            javaParams.getVMParametersList().add("-Dmule.debug.port=" + getPort());

            javaParams.getVMParametersList().add("-Xdebug");
            javaParams.getVMParametersList().add("-agentlib:jdwp=transport=dt_socket,address=" + getHost() + ":" + getPort() + ",suspend=n,server=n");
        }

        javaParams.getVMParametersList().add("-Xms1024m");
        javaParams.getVMParametersList().add("-Xmx1024m");
        javaParams.getVMParametersList().add("-XX:+HeapDumpOnOutOfMemoryError");
        javaParams.getVMParametersList().add("-XX:+AlwaysPreTouch");
        javaParams.getVMParametersList().add("-XX:NewSize=512m");
        javaParams.getVMParametersList().add("-XX:MaxNewSize=512m");
        javaParams.getVMParametersList().add("-XX:MaxTenuringThreshold=8");


        // VM Args
        String vmArgs = this.getVmArgs();
        if (vmArgs != null) {
            javaParams.getVMParametersList().addParametersString(vmArgs);
        }

        // All done, run it
        return javaParams;
    }

    @Override
    protected boolean ansiColoringEnabled() {
        return true;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        deployApp();
        return super.startProcess();
    }

    private boolean isClearAppData() {
        String clearDataString = model.getClearData();

        boolean clearData = (MuleRunnerEditor.CLEAR_DATA_ALWAYS.equals(clearDataString));

        if (!clearData) {
            if (MuleRunnerEditor.CLEAR_DATA_PROMPT.equals(clearDataString)) {
                int result = Messages.showYesNoDialog("Clear the application data (caches, object stores) before the launch?", "Clear Application Data", AllIcons.General.QuestionDialog);
                clearData = (result == Messages.YES);
            }
        }

        return clearData;
    }

    private void deployApp() throws ExecutionException {

        boolean clearData = isClearAppData();

        try {
            FileUtils.cleanDirectory(muleBaseDirectory.getAppsFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Module[] modules = model.getModules();

        //We need to sort out domains and apps and deploy domains first
        List<Module> domainsList = new ArrayList<>();
        List<Module> appsList = new ArrayList<>();

        for (Module m : modules) {
            if (MuleModuleUtils.isMuleDomainModule(m))
                domainsList.add(m);
            else
                appsList.add(m);
        }

        for (Module m : domainsList) {
//            if (clearData) {
//                File moduleAppData = new File(muleBaseDirectory.getAppDataFolder(), m.getName());
//                FileUtil.delete(moduleAppData);
//            }
            //Get the zip and deploy it
            final File file = MuleAppManager.getInstance(model.getProject()).getMuleApp(m);

            try {
                File destination = muleBaseDirectory.getDomainsFolder();
                //Domains require FQN including version, e.g. my-domain-1.0.0-SNAPSHOT-mule-domain
                //Apps don't. Makes no sense but it is what it is.
                FileUtil.copy(file, new File(destination, file.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Module m : appsList) {
            if (clearData) {
                File moduleAppData = new File(muleBaseDirectory.getAppDataFolder(), m.getName());
                FileUtil.delete(moduleAppData);
            }
            //Get the zip and deploy it
            final File file = MuleAppManager.getInstance(model.getProject()).getMuleApp(m);

            try {
                File destination = muleBaseDirectory.getAppsFolder();
                //Domains require FQN including version, e.g. my-domain-1.0.0-SNAPSHOT-mule-domain
                //Apps don't. Makes no sense but it is what it is.
                FileUtil.copy(file, new File(destination, m.getName() + MuleAppHandler.MULE_APP_SUFFIX));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the "VM Args" parameter
     *
     * @return String
     */
    protected String getVmArgs() {
        String vmArgs = model.getVmArgs();
        return vmArgs != null && !vmArgs.isEmpty() ? vmArgs : null;
    }


    @Override
    public String getHost() {
        return "0.0.0.0";
    }

    @Override
    public int getPort() {
        try {
            return Integer.parseInt(model.getDebugPort());
        } catch (Exception e) {
            logger.error("Unable to parse debug port, returning default 6666 : ", e);
            return 6666;
        }
    }
}
