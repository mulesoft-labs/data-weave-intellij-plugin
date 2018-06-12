package org.mule.tooling.runtime.schema;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.xml.impl.schema.XmlNSDescriptorImpl;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.mule.tooling.runtime.tooling.ToolingClientManager;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MuleSchemaManager implements ModuleComponent {

    public static final String MULE_SCHEMAS = "mule.schemas";


    public static final String SCHEMA_LOCATION_PROP = "schemaLocation";
    public static final String NAMESPACE_PROP = "namespace";

    private static String MULE_NS = "http://www.mulesoft.org/schema/mule/core";
    private static String MULE_CORE_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/core/current/mule-core.xsd";
    private static String MULE_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/core/current/mule.xsd";

    private static String MODULE_NS = "http://www.mulesoft.org/schema/mule/module";
    private static String MODULE_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/module/current/mule-module.xsd";

    private static String DOMAIN_NS = "http://www.mulesoft.org/schema/mule/domain";
    private static String DOMAIN_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/domain/current/mule-domain.xsd";

    private static String DOCUMENTATION_NS = "http://www.mulesoft.org/schema/mule/documentation";
    private static String DOCUMENTATION_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/mule-documentation.xsd";

    private static String SPRING_BEANS_NS = "http://www.springframework.org/schema/beans";
    private static String SPRING_BEANS_SCEMA_LOCATION = "http://www.springframework.org/schema/beans/spring-beans-3.0.xsd";

    private static String MULE_SCHEMA_DOC_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/schemadoc/current/mule-schemadoc.xsd";
    private static String MULE_SCHEMA_DOC_NS = "http://www.mulesoft.org/schema/mule/schemadoc";

    public static final String MULE_NS_PREFIX = "http://www.mulesoft.org/schema/mule/";


    private Map<String, XmlInfo> defaultSchemas;
    private Map<String, XmlInfo> moduleSchemas;
    private Map<String, XmlInfo> muleClasspathSchemas;

    private final Project project;
    private Module myModule;

    public MuleSchemaManager(Module myModule) {
        this.myModule = myModule;
        this.project = myModule.getProject();
        this.defaultSchemas = new HashMap<>();
        this.moduleSchemas = new ConcurrentHashMap<>();
        this.muleClasspathSchemas = new HashMap<>();
    }


    public String getMuleVersion(Module module) {
        return ToolingClientManager.MULE_VERSION;
    }


    public File getMuleSchemasFolder(Module module) {
        File ideHome = ToolingClientManager.getInstance(module).getMuleIdeWorkingDir();
        File schemas = new File(new File(ideHome, getMuleVersion(module)), "schemas");
        if (!schemas.exists()) {
            schemas.mkdirs();
        }
        return schemas;
    }

    @Override
    public void moduleAdded() {
        if (MuleModuleUtils.isMuleModule(myModule)) {
            //Load default schemas
            addXmlFileFromResource(project, myModule, MULE_NS, MULE_SCHEMA_LOCATION, "schemas/mule-core.xsd", "mule-core.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, MULE_NS, MULE_CORE_SCHEMA_LOCATION, "schemas/mule-core.xsd", "mule-core.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, DOMAIN_NS, DOMAIN_SCHEMA_LOCATION, "schemas/mule-domain.xsd", "mule-domain.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, DOCUMENTATION_NS, DOCUMENTATION_SCHEMA_LOCATION, "schemas/mule-documentation.xsd", "mule-documentation.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, MODULE_NS, MODULE_SCHEMA_LOCATION, "schemas/mule-module.xsd", "mule-module.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, SPRING_BEANS_NS, SPRING_BEANS_SCEMA_LOCATION, "schemas/spring-beans-3.0.xsd", "spring-beans-3.0.xsd", defaultSchemas);
            addXmlFileFromResource(project, myModule, MULE_SCHEMA_DOC_NS, MULE_SCHEMA_DOC_SCHEMA_LOCATION, "schemas/mule-schemadoc.xsd", "mule-schemadoc.xsd", defaultSchemas);
            reloadModuleDependencies();
        }
    }

    public synchronized void reloadModuleDependencies() {
        ToolingClientManager.getInstance(myModule).addStartListener(() -> {
            ReadAction.run(() -> {
                loadMuleSchemasInClasspath();
                loadSchemasFromTooling();
            });
        });
    }

    public Optional<XmlFile> getSchema(String uri) {
        if (uri.equals(DOCUMENTATION_NS)) {
            return Optional.empty();
        }
        Optional<XmlInfo> xmlInfo = getXmlInfo(uri);
        if (!xmlInfo.isPresent() && uri.startsWith(MULE_NS_PREFIX)) {
            reloadModuleDependencies();
        }
        return xmlInfo.map(XmlInfo::getSchemaFile);
    }

    public Optional<XmlInfo> getXmlInfo(String uri) {
        final XmlInfo xmlFile = defaultSchemas.getOrDefault(uri, muleClasspathSchemas.getOrDefault(uri, moduleSchemas.get(uri)));
        return Optional.ofNullable(xmlFile);
    }

    public Optional<String> getSchemaLocation(String uri) {
        if (uri.equals(DOCUMENTATION_NS)) {
            return Optional.empty();
        }
        return getXmlInfo(uri).map(XmlInfo::getLocation);
    }

    public List<XmlInfo> getSchemas() {
        final ArrayList<XmlInfo> result = new ArrayList<>();
        result.addAll(defaultSchemas.values());
        result.addAll(muleClasspathSchemas.values());
        result.addAll(moduleSchemas.values());
        return result;
    }

    public void loadSchemasFromTooling() {
        MavenProject mavenProject = MuleModuleUtils.getMavenProject(myModule);
        if (mavenProject != null) {
            List<MavenArtifact> dependencies = mavenProject.getDependencies();
            for (MavenArtifact dependency : dependencies) {
                if (ToolingClientManager.MULE_PLUGIN.equalsIgnoreCase(dependency.getClassifier())) {
                    String artifactId = dependency.getArtifactId();
                    String groupId = dependency.getGroupId();
                    String version = dependency.getVersion();
                    addSchemaFile(project, myModule, groupId, artifactId, version, moduleSchemas);
                }
            }
        }
    }

    public void loadMuleSchemasInClasspath() {
        final Map<String, String> schemaUrlsAndFileNames = getSchemasFromSpringSchemas(myModule);
        for (String url : schemaUrlsAndFileNames.keySet()) {
            final String fileName = schemaUrlsAndFileNames.get(url);
            final String relativePath = fileName.startsWith("/") ? fileName : "/" + fileName;
            final Set<FileType> fileTypes = Collections.singleton(FileTypeManager.getInstance().getFileTypeByFileName(relativePath));
            final List<VirtualFile> fileList = new ArrayList<>();

            FileBasedIndex.getInstance().processFilesContainingAllKeys(FileTypeIndex.NAME, fileTypes, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule), null, virtualFile -> {
                if (virtualFile.getPath().endsWith(relativePath)) {
                    fileList.add(virtualFile);
                }
                return true;
            });
            if (!fileList.isEmpty()) {
                final VirtualFile virtualFile = fileList.get(0);
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile instanceof XmlFile) {
                    final XmlFile xmlFile = (XmlFile) psiFile;
                    final XmlDocument document = xmlFile.getDocument();
                    String defaultNamespace = url;
                    if (document != null) {
                        final PsiMetaData metaData = document.getMetaData();
                        if (metaData instanceof XmlNSDescriptorImpl) {
                            XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
                            defaultNamespace = descriptor.getDefaultNamespace();
                        }
                    }
                    final XmlInfo xmlInfo = new XmlInfo(project, virtualFile, url, defaultNamespace);
                    muleClasspathSchemas.put(url, xmlInfo);
                    muleClasspathSchemas.put(defaultNamespace, xmlInfo);
                }
            }
        }
    }


    @Nullable
    public void addSchemaFile(Project project, Module module, String groupId, String artifactId, String version, Map<String, XmlInfo> target) {
        try {
            final String name = artifactId + ".xsd";
            final File schemaTargetFolder = new File(new File(new File(getMuleSchemasFolder(module), groupId), artifactId), version);
            final File schemaFile = new File(schemaTargetFolder, name);
            final File propertiesFile = new File(schemaTargetFolder, artifactId + ".properties");
            if (schemaFile.exists() && propertiesFile.exists()) {
                Properties properties = new Properties();
                properties.load(new FileReader(propertiesFile));
                addSchema(project, schemaFile, properties.getProperty(NAMESPACE_PROP), properties.getProperty(SCHEMA_LOCATION_PROP), target);
            } else {
                //TODO check if it exists before
                final Optional<ToolingClientManager.SchemaPair> schema = ToolingClientManager.getInstance(module).getSchema(groupId, artifactId, version);
                if (schema.isPresent()) {
                    if (!schemaTargetFolder.exists()) {
                        schemaTargetFolder.mkdirs();
                    }
                    final ToolingClientManager.SchemaPair schemaPair = schema.get();
                    final String schemaLocation = schemaPair.getSchemaLocation();
                    final String namespace = schemaPair.getNamespace();
                    final String schemaContent = schemaPair.getSchemaContent();
                    final Properties schemaProperties = new Properties();
                    schemaProperties.put(NAMESPACE_PROP, namespace);
                    schemaProperties.put(SCHEMA_LOCATION_PROP, schemaLocation);
                    schemaProperties.store(new FileWriter(propertiesFile), "File that contains artifact information.");
                    createFileAndAdd(project, schemaFile, namespace, schemaLocation, schemaContent, target);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createFileAndAdd(Project project, File schemaFile, String namespace, String schemaLocation, String schemaContent, Map<String, XmlInfo> target) throws IOException {
        try (FileWriter output = new FileWriter(schemaFile)) {
            IOUtils.write(schemaContent, output);
        }
        addSchema(project, schemaFile, namespace, schemaLocation, target);
    }

    public void addSchema(Project project, File schemaFile, String namespace, String schemaLocation, Map<String, XmlInfo> target) {
        final VirtualFile fileByUrl = LocalFileSystem.getInstance().findFileByIoFile(schemaFile);
        if (fileByUrl != null) {
            XmlInfo xmlInfo = new XmlInfo(project, fileByUrl, schemaLocation, namespace);
            target.put(namespace, xmlInfo);
            target.put(schemaLocation, xmlInfo);
        }

    }

    @Nullable
    public void addXmlFileFromResource(Project project, Module module, String namespace, String schemaLocation, String schema, String name, Map<String, XmlInfo> target) {
        try {
            File system = new File(getMuleSchemasFolder(module), "system");
            if (!system.exists()) {
                system.mkdirs();
            }
            final File schemaFile = new File(system, name);

            final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(schema);
            final String schemaContent = IOUtils.toString(resourceAsStream, "UTF-8");
            if (schemaFile.exists()) {
                schemaFile.delete();
            }
            createFileAndAdd(project, schemaFile, namespace, schemaLocation, schemaContent, target);
        } catch (IOException e) {
            //Ignore
            e.printStackTrace();
        }
    }


    private Map<String, String> parseSpringSchemas(String springSchemasContent) {
        Map<String, String> schemaUrlsAndFileNames = new HashMap<>();
        for (String line : springSchemasContent.split("\n")) {
            if (line != null && !line.startsWith("#") && line.contains("=")) {
                String url = line.substring(0, line.indexOf("=")).replaceAll("\\\\", "");
                String fileName = line.substring(line.indexOf("=") + 1);

                if (schemaUrlsAndFileNames.containsValue(fileName)) {
                    if (url.contains("current")) { //Avoid duplicates and prefer URL with "current"
                        schemaUrlsAndFileNames.put(url, fileName);
                    }
                } else {
                    schemaUrlsAndFileNames.put(url, fileName);
                }
            }
        }
        return schemaUrlsAndFileNames;

    }

    private Map<String, String> getSchemasFromSpringSchemas(@NotNull Module module) {
        return ReadAction.compute(() -> {
            Map<String, String> schemasMap = new HashMap<>();
            PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), MULE_SCHEMAS, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));

            for (PsiFile nextSpringS : psiFiles) {
                VirtualFile springSchemasFile = nextSpringS.getVirtualFile();
                if (springSchemasFile != null) {
                    try {
                        String springSchemasContent = new String(springSchemasFile.contentsToByteArray(), springSchemasFile.getCharset());
                        schemasMap.putAll(parseSpringSchemas(springSchemasContent));
                    } catch (Exception e) {

                    }
                }
            }
            return schemasMap;
        });

    }


    public static MuleSchemaManager getInstance(Module myModule) {
        return myModule.getComponent(MuleSchemaManager.class);
    }

    public static class XmlInfo {

        private Project project;
        private VirtualFile schemaFile;
        private String location;
        private String namespace;

        public XmlInfo(Project project, VirtualFile schemaFile, String location, String namespace) {
            this.project = project;
            this.schemaFile = schemaFile;
            this.location = location;
            this.namespace = namespace;
        }

        public XmlFile getSchemaFile() {
            final PsiFile psiFile = PsiManager.getInstance(project).findFile(schemaFile);
            if (psiFile instanceof XmlFile) {
                return (XmlFile) psiFile;
            }
            return null;
        }

        public String getLocation() {
            return location;
        }

        public String getNamespace() {
            return namespace;
        }
    }
}
