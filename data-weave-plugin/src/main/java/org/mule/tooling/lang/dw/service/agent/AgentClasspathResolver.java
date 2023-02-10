package org.mule.tooling.lang.dw.service.agent;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

interface AgentClasspathResolver {

    List<String> resolveClasspathJars();
}

class ResourceBasedAgentClasspathResolver implements AgentClasspathResolver {
    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String AGENT_FOLDER = "agent-server-libs";
    private static final String DESTINATION_FOLDER = "data-weave-agent";
    private static final List<String> RESOURCES = Arrays.asList(AGENT_FOLDER + "/agent-server.jar", AGENT_FOLDER + "/agent-api.jar");
    private static final String JAR_EXTENSION = ".jar";
    private static final Logger LOGGER = Logger.getInstance(ResourceBasedAgentClasspathResolver.class);
    private volatile boolean initialized = false;
    private Optional<FolderBasedAgentClasspathResolver> agentClasspathResolver;
    private Optional<FolderBasedAgentClasspathResolver> agentClasspathResolver() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    final Optional<File> maybeLibsDirectory = createLibsDirectory();
                    agentClasspathResolver = maybeLibsDirectory.map(FolderBasedAgentClasspathResolver::new);
                    this.initialized = true;
                }
            }
        }
        return agentClasspathResolver;
    }

    private void copyJar(String resource, Path targetDirectory) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
        if (is != null) {
            try {
                String baseName = FilenameUtils.getBaseName(resource);
                Path copied = targetDirectory.resolve(baseName + JAR_EXTENSION);
                Files.copy(is, copied, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
               // Nothing to do
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                   // Nothing to do
                }
            }
        }
    }

    private Optional<File> createLibsDirectory() {
        URL url = getClass().getClassLoader().getResource(AGENT_FOLDER);
        if (url != null) {
            if (!TMP_DIR.exists()) {
                throw new RuntimeException("The specified temporary " + TMP_DIR.getAbsolutePath() + " directory does not exits. Please create the directory or provide a different one.");
            }
            // Create target directory
            try {
                Path targetDirectory = Files.createTempDirectory(DESTINATION_FOLDER);
                // Copy resources
                for (final String resource : RESOURCES) {
                    copyJar(resource, targetDirectory);
                }
                return of(targetDirectory.toFile());
            } catch (IOException e) {
                // Nothing to do
            }
        }
        return empty();
    }

    @Override
    public List<String> resolveClasspathJars() {
        List<String> jars = Collections.emptyList();
        Optional<FolderBasedAgentClasspathResolver> agentClasspathResolver = agentClasspathResolver();
        if (agentClasspathResolver.isPresent()) {
            jars = agentClasspathResolver.get().resolveClasspathJars();
        }
        if (jars.isEmpty()) {
            LOGGER.error("Could not resolve agent classpath jars");
        }
        return jars;
    }
}

class FolderBasedAgentClasspathResolver implements AgentClasspathResolver {
    private static final String JAR_EXTENSION = "jar";

    private final File directory;

    private volatile boolean initialized = false;

    private List<String> jars = null;

    public FolderBasedAgentClasspathResolver(File directory) {
        this.directory = directory;
    }

    private List<String> getJars() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    if (!directory.exists() || !directory.isDirectory()) {
                        jars = Collections.emptyList();
                    } else {
                        jars = Stream.of(directory.listFiles(pathname -> {
                            boolean isJar = FilenameUtils.getExtension(pathname.getName()).equals(JAR_EXTENSION);
                            return pathname.isFile() && pathname.canRead() && !pathname.isHidden() && isJar;
                        })).map(File::getAbsolutePath).collect(toList());
                    }
                    this.initialized = true;
                }
            }
        }
        return jars;
    }

    @Override
    public List<String> resolveClasspathJars() {
        return getJars();
    }
}