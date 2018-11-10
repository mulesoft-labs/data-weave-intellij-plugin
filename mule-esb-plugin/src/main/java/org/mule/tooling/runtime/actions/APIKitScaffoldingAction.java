package org.mule.tooling.runtime.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.plugin.logging.Log;
//import org.mule.tooling.lang.raml.file.RamlFileType;
//import org.mule.tooling.lang.raml.util.RamlIcons;
import org.mule.tooling.lang.raml.file.RamlFileType;
import org.mule.tooling.lang.raml.util.RamlIcons;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;
import org.mule.tools.apikit.Scaffolder;
import org.mule.tools.apikit.ScaffolderAPI;
import org.mule.tools.apikit.model.RuntimeEdition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class APIKitScaffoldingAction extends AnAction
{

    final static Logger logger = Logger.getInstance(APIKitScaffoldingAction.class);

    public APIKitScaffoldingAction()
    {
        super("Generate Flows", "Generate APIKit scaffolding from the RAML definition", RamlIcons.RamlFileType);
//        super("Generate Flows", "Generate APIKit scaffolding from the RAML definition", AllIcons.FileTypes.Any_type);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        final Project project = anActionEvent.getProject();
        final VirtualFile moduleContentRoot = ProjectRootManager.getInstance(project).getFileIndex().getContentRootForFile(file);
        String appPath = moduleContentRoot.getPath() + File.separator + MuleDirectoriesUtils.SRC_MAIN_MULE;

        logger.debug("*** APP PATH IS " + appPath);

        final File appDir = new File(appPath);

        logger.debug("*** APP DIR IS DIRECTORY = " + appDir.isDirectory());

        final List<File> ramlFiles = new ArrayList<File>();
        final File ramlFile = new File(file.getPath());
        ramlFiles.add(ramlFile);

//TODO - list all RAML files in the project and add them too?
//        Collection<VirtualFile> ramlFilesInProject = FilenameIndex.getAllFilesByExt(project, "raml", GlobalSearchScope.projectScope(project));
//        for (VirtualFile vFile : ramlFilesInProject) {
//            ramlFiles.add(new File(vFile.getPath()));
//        }

        //TODO - go through the list of modules and see if any one of them is a Mule domain
        /*
            1. Get the POM of the module
            2. See if one of the dependencies is a domain and get its artifact ID
            3. Find module with artifact ID equals #2
            4. Get its directory
         */

        try
        {
            //TODO min Mule version should be derived from the module's artifact JSON
            new IdeaScaffolderAPI().run(ramlFiles, appDir, null, "4.1.1", RuntimeEdition.EE);
        } catch (RuntimeException e) {
            logger.error("FINALLY CAUGHT RAML ERROR! ", e);
        }
        finally
        {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    file.getParent().getParent().refresh(false, true);
                    moduleContentRoot.refresh(false, true);
                }
            });
        }
    }

    @Override
    public void update(AnActionEvent anActionEvent)
    {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        boolean isRAML = false;

        if (file != null)
        {
            try {
                isRAML = RamlFileType.getInstance().getDefaultExtension().equalsIgnoreCase(file.getExtension());
//                isRAML = true;
            } catch (Throwable e) {
                logger.debug("Can't decide if it's RAML or not, will try another option", e);

                try {
                    String contents = new String(file.contentsToByteArray());
                    isRAML = contents.startsWith("#%RAML");
                } catch (Throwable e1) {
                    isRAML = false;
                }
            }

            anActionEvent.getPresentation().setEnabled(isRAML);
            anActionEvent.getPresentation().setVisible(isRAML);
        }

    }

    //============================================================================================================================================
    class IdeaEventLogger implements Log {

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(CharSequence charSequence) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", charSequence.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public void debug(CharSequence charSequence, Throwable throwable) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", charSequence.toString() + " : " + throwable.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public void debug(Throwable throwable) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", throwable.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        @Override
        public void info(CharSequence charSequence) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", charSequence.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public void info(CharSequence charSequence, Throwable throwable) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", charSequence.toString() + " : " + throwable.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public void info(Throwable throwable) {
            Notification notification = new Notification("APIKit Scaffolding", "APIKit", throwable.toString(), NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            notification.getBalloon().hide();
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void warn(CharSequence charSequence) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", charSequence.toString(), NotificationType.WARNING));

        }

        @Override
        public void warn(CharSequence charSequence, Throwable throwable) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", charSequence.toString() + " : " + throwable.toString(),
                    NotificationType.WARNING));
        }

        @Override
        public void warn(Throwable throwable) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", throwable.toString(),
                    NotificationType.WARNING));

        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(CharSequence charSequence) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", charSequence.toString(), NotificationType.ERROR));
        }

        @Override
        public void error(CharSequence charSequence, Throwable throwable) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", charSequence.toString() + " : " + throwable.toString(),
                    NotificationType.ERROR));
        }

        @Override
        public void error(Throwable throwable) {
            Notifications.Bus.notify(new Notification("APIKit Scaffolding", "APIKit", throwable.toString(),
                    NotificationType.ERROR));
        }
    }

    class IdeaScaffolderAPI extends ScaffolderAPI {
        private final List<String> apiExtensions = Arrays.asList(".yaml", ".raml", ".yml");
        private final List<String> appExtensions = Arrays.asList(".xml");


        @Override
        public void run(List<File> ramlFiles, File appDir, File domainDir, String minMuleVersion, RuntimeEdition runtimeEdition) {
            List<String> ramlFilePaths = retrieveFilePaths(ramlFiles, apiExtensions);
            List<String> muleXmlFiles = retrieveFilePaths(appDir, appExtensions);
            IdeaEventLogger log = new IdeaEventLogger();
            String domain = null;
            if (domainDir != null) {
                List<String> domainFiles = retrieveFilePaths(domainDir, appExtensions);
                if (domainFiles.size() > 0) {
                    domain = domainFiles.get(0);
                    if (domainFiles.size() > 1) {
                        log.info("There is more than one domain file inside of the domain folder. The domain: " + domain + " will be used.");
                    }
                }
            }
            Scaffolder scaffolder;
            try {
                scaffolder = Scaffolder.createScaffolder(log, appDir, ramlFilePaths, muleXmlFiles, domain, minMuleVersion, runtimeEdition);
            } catch (Exception e) {
                throw new RuntimeException("Error executing scaffolder", e);
            }
            scaffolder.run();
        }

//        @Override
//        public void execute(List<File> ramlFiles, File appDir, File domainDir, String muleVersion) {
//            List<String> ramlFilePaths = this.retrieveFilePaths(ramlFiles, apiExtensions);
//            List<String> muleXmlFiles = this.retrieveFilePaths(appDir, appExtensions);
//
//            String domain = null;
//            if (domainDir != null) {
//                List<String> domainFiles = this.retrieveFilePaths(domainDir, appExtensions);
//                if (domainFiles.size() > 0) {
//                    domain = (String)domainFiles.get(0);
//                    if (domainFiles.size() > 1) {
//                        log.info("There is more than one domain file inside of the domain folder. The domain: " + domain + " will be used.");
//                    }
//                }
//            }
//
//            Scaffolder scaffolder;
//            try {
//                scaffolder = Scaffolder.createScaffolder(log, appDir, ramlFilePaths, muleXmlFiles, domain, muleVersion);
//            } catch (Exception var11) {
//                throw new RuntimeException("Error executing scaffolder", var11);
//            }
//
//            scaffolder.run();
//        }

        private List<String> retrieveFilePaths(File dir, List<String> extensions) {
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("File " + dir.getName() + " must be a directory");
            } else {
                return this.retrieveFilePaths((List)(new ArrayList(Arrays.asList(dir.listFiles()))), extensions);
            }
        }

        private List<String> retrieveFilePaths(List<File> files, List<String> extensions) {
            List<String> filePaths = new ArrayList();
            if (files != null) {
                Iterator i$ = files.iterator();

                while(i$.hasNext()) {
                    File file = (File)i$.next();
                    if (this.containsValidExtension(file, extensions)) {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }

            return filePaths;
        }

        private boolean containsValidExtension(File file, List<String> extensions) {
            Iterator i$ = extensions.iterator();

            String extension;
            do {
                if (!i$.hasNext()) {
                    return false;
                }

                extension = (String)i$.next();
            } while(!file.getName().toLowerCase().endsWith(extension));

            return true;
        }
    }
}
