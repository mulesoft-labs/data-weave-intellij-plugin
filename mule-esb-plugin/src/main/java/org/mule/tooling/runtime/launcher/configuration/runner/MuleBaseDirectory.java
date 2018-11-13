package org.mule.tooling.runtime.launcher.configuration.runner;

import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstraction of the runtime/container structure to consume some directories from it.
 * Useful to rely on the MULE_BASE feature if needed, see static methods.
 */
public interface MuleBaseDirectory {

    String FOLDER_APPS_DATA = ".mule";
    String FOLDER_APPS = "apps";
    String FOLDER_DOMAINS = "domains";

    File getMuleBase();

    default File getAppDataFolder() {
        return new File(getMuleBase(), FOLDER_APPS_DATA);
    }

    default File getAppsFolder() {
        return new File(getMuleBase(), FOLDER_APPS);
    }

    default File getDomainsFolder() {
        return new File(getMuleBase(), FOLDER_DOMAINS);
    }

    /**
     * Recreates the needed structure to run an app relying on the MULE_BASE feature: it won't edit the runtime's folder
     * structure at all, making it useful to run multiple apps using the same distro in different IDEs without colliding
     * (unless there are conflicts such as using the same ports, and so on..)
     *
     * @param muleHome points to the absolute path of the container that's going to be used (needed to copy files from)
     * @param projectName name of the project to create a folder to be later used to drop the application and stuff
     * @param muleVersion runtime version to accommodate the directory's structure
     * @return an abstraction of the current mule distro structure
     */
    static MuleBaseDirectory newFrom(String muleHome, String projectName, String muleVersion){
        return new ProjectMuleBaseFolder(muleHome, projectName, muleVersion);
    }

    /**
     * Actual container structure of /apps, /domains, etc...
     *
     * @param muleHome points to the absolute path of the container that's going to be used
     * @return the actual mule distro structure (points to the same directory where the {@code muleHome} is)
     */
    static MuleBaseDirectory sameMuleBase(String muleHome){
        return () -> new File(muleHome);
    }

    /**
     * Mimics container structure relying on the MULE_BASE feature, by recreating folders.
     * The resulting root folder will be something like (if {@code muleVersion} = 4.1.4)
     * <pre>
     *  ➜  ~ tree ~/.mule_ide/4.1.4/mule_base_apps/mymuleapp
     *  ~/.mule_ide/4.1.4/mule_base_apps/mymuleapp
     *  ├── apps
     *  │   ├── mymuleapp-mule-application
     *  │   │   ├── ...
     *  │   │   └── my-mule-app.xml
     *  │   └── mymuleapp-mule-application-anchor.txt
     *  ├── conf
     *  │   └── log4j2.xml
     *  ├── domains
     *  │   ├── default
     *  │   └── default-anchor.txt
     *  └── services
     * </pre>
     */
    class ProjectMuleBaseFolder implements MuleBaseDirectory {

        private static final String FOLDER_MULE_BASE_APPS = "mule_base_apps";
        private final File muleBase;

        ProjectMuleBaseFolder(String muleHome, String projectName, String muleVersion) {
            final File muleHomeFolder = new File(muleHome);
            final File muleBaseDirectoryApps = getMuleBaseDirectoryApps(muleVersion);
            this.muleBase = getOrCreateFolder(muleBaseDirectoryApps, projectName);
            getOrCreateFolder(muleBase, FOLDER_APPS_DATA);
            getOrCreateFolder(muleBase, FOLDER_APPS);
            try {
                final File conf = getOrCreateFolder(muleBase, "conf");
                if (conf.list().length == 0) {
                    final Path log4j2File = muleHomeFolder.toPath().resolve("conf").resolve("log4j2.xml");
                    Files.copy(log4j2File, conf.toPath().resolve("log4j2.xml"));
                }

                final File domains = getOrCreateFolder(muleBase, FOLDER_DOMAINS);
                //for some reason the runtime wipes out the `domains/default` folder on each run, see https://www.mulesoft.org/jira/browse/MULE-16039
                final Path defaultAnchorFile = domains.toPath().resolve("default-anchor.txt");
                Files.deleteIfExists(defaultAnchorFile);
                getOrCreateFolder(domains, "default");
                Files.write(defaultAnchorFile, "content to avoid removing the domain in the anchor file".getBytes());

                final File services = getOrCreateFolder(muleBase, "services");
                if (services.list().length == 0) {
                    //Deploying without all the services will fail, see https://www.mulesoft.org/jira/browse/MULE-16032
                    final File muleHomeServices = new File(muleHomeFolder, "services");
                    FileUtils.copyDirectory(muleHomeServices, services);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public File getMuleBase() {
            return muleBase;
        }

        private File getMuleBaseDirectoryApps(String muleVersion) {
            File muleBaseDirectory = new File(MuleDirectoriesUtils.getMuleHomeDirectory(muleVersion), FOLDER_MULE_BASE_APPS);
            if (!muleBaseDirectory.exists()) {
                muleBaseDirectory.mkdirs();
            }
            return muleBaseDirectory;
        }

        @NotNull
        private File getOrCreateFolder(File parent, String folder) {
            File file = new File(parent, folder);
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("The folder '%s' cannot be created under '%s', failing.", folder, parent.toString()), e);
            }
            return file;
        }
    }


}
