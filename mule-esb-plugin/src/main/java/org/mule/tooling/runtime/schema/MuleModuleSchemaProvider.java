package org.mule.tooling.runtime.schema;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.mule.tooling.runtime.tooling.MuleRuntimeServerManager;
import org.mule.tooling.runtime.tooling.ToolingArtifactManager;
import org.mule.tooling.runtime.tooling.ToolingRuntimeTopics;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.mule.tooling.runtime.schema.MuleSchemaRepository.COM_MULESOFT_MUNIT;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.COM_MULESOFT_RUNTIME;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.DOCUMENTATION_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.DOMAIN_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MODULE_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_CORE_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_EE_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_MODULE_BATCH;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_MODULE_TLS_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MULE_SCHEMADOC_ARTIFACT_ID;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MUNIT_RUNNER;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.MUNIT_TOOLS;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_MULE_RUNTIME;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_MULE_TOOLING;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_SPRINGFRAMEWORK;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.SPRING_BEANS_ARTIFACT_ID;
import static org.mule.tooling.runtime.util.MuleModuleUtils.MULE_EXTENSION_PACKAGING;

/**
 * Handles schema for a given module. It delegates on the MuleSchemaRepository
 * It will use the module dependencies to resolve visibilty of what schemas are reachable and not.
 */
public class MuleModuleSchemaProvider implements ModuleComponent {

  public static final String MULE_SCHEMAS = "mule.schemas";
  public static final String MUNIT_EXTENSIONS_PLUGIN = "munit-extensions-maven-plugin";


  private Map<String, SchemaInformation> moduleSchemas;

  private final Project project;
  private Module myModule;
  private volatile boolean initialized = false;
  private volatile boolean initializing = false;

  public MuleModuleSchemaProvider(Module myModule) {
    this.myModule = myModule;
    this.project = myModule.getProject();
    this.moduleSchemas = new ConcurrentHashMap<>();
  }

  public Optional<SchemaInformation> getSchema(String uri) {
    initializeIfRequired();
    return Optional.ofNullable(moduleSchemas.get(uri));
  }

  public Collection<SchemaInformation> getSchemas() {
    initializeIfRequired();
    return this.moduleSchemas.values();
  }

  public Optional<String> getSchemaLocation(String uri) {
    return getSchema(uri).map(SchemaInformation::getSchemaLocation);
  }

  private String getMuleVersion() {
    return MuleRuntimeServerManager.getMuleVersionOf(myModule);
  }

  @Nullable
  private String getMunitVersion() {
    return MuleRuntimeServerManager.getMunitVersionOf(myModule);
  }

  private void initializeIfRequired() {
    if (!initialized && !initializing) {
      initializing = true;

      loadDefaultSchemas();
      loadSchemasFromTooling();

      // not working well and currently not being used
      // loadMuleSchemasInClasspath();

      //Listen to classpath changes
      project.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
        @Override
        public void rootsChanged(ModuleRootEvent event) {
          moduleSchemas.clear();
          loadDefaultSchemas();
          loadSchemasFromTooling();

          // not working well and currently not being used
          //   loadMuleSchemasInClasspath();
        }
      });
      //Listen to tooling runtime being loaded
      ApplicationManager.getApplication().getMessageBus().connect().subscribe(ToolingRuntimeTopics.TOOLING_STARTED, muleVersion -> loadSchemasFromTooling());
      initialized = true;
      initializing = false;
    }
  }

  private void loadDefaultSchemas() {
    String runtimeVersion = getMuleVersion();
    loadSchema(ORG_MULE_RUNTIME, MULE_CORE_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_RUNTIME, MULE_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_RUNTIME, MODULE_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_RUNTIME, MULE_MODULE_TLS_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_RUNTIME, DOMAIN_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_SPRINGFRAMEWORK, SPRING_BEANS_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_RUNTIME, MULE_SCHEMADOC_ARTIFACT_ID, runtimeVersion);
    loadSchema(ORG_MULE_TOOLING, DOCUMENTATION_ARTIFACT_ID, runtimeVersion);
    loadSchema(COM_MULESOFT_RUNTIME, MULE_EE_ARTIFACT_ID, runtimeVersion);
    loadSchema(COM_MULESOFT_RUNTIME, MULE_MODULE_BATCH, runtimeVersion);
  }

  private void loadSchemasFromTooling() {
    MavenProject mavenProject = MuleModuleUtils.getMavenProject(myModule);
    if (mavenProject != null) {

      if (MULE_EXTENSION_PACKAGING.equals(mavenProject.getPackaging())) {
        loadCurrentExtensionSchemas(mavenProject);
      }

      List<MavenArtifact> dependencies = mavenProject.getDependencies();
      for (MavenArtifact dependency: dependencies) {
        if (ToolingArtifactManager.MULE_PLUGIN.equalsIgnoreCase(dependency.getClassifier())) {
          String artifactId = dependency.getArtifactId();
          String groupId = dependency.getGroupId();
          String version = dependency.getVersion();
          loadSchema(groupId, artifactId, version);
        }
      }
    }
    if(isMunitInstalled()) {
      loadSchema(COM_MULESOFT_MUNIT, MUNIT_TOOLS, getMunitVersion());
    }
  }

  private boolean isMunitInstalled() {
    return getMunitVersion() != null;
  }

  private void loadCurrentExtensionSchemas(MavenProject mavenProject) {
    MavenId id = mavenProject.getMavenId();
    // This requires the current project to be installed in the local repo for now.
    loadSchema(id.getGroupId(), id.getArtifactId(), id.getVersion());

    mavenProject.getDeclaredPlugins().stream()
        .filter(p -> MUNIT_EXTENSIONS_PLUGIN.equals(p.getMavenId().getArtifactId())).findAny()
        .ifPresent(p -> {
          String munitVersion = getMunitVersion();
          loadSchema(COM_MULESOFT_MUNIT, MUNIT_TOOLS, munitVersion);
          loadSchema(COM_MULESOFT_MUNIT, MUNIT_RUNNER, munitVersion);
        });
  }

  private void loadSchema(String groupId, String artifactId, String version) {
    final String muleVersion = ReadAction.compute(() -> getMuleVersion());
    final Optional<SchemaInformation> schemaInformation = MuleSchemaRepository.getInstance(muleVersion).loadSchemaFromCoordinate(myModule.getProject(), groupId, artifactId, version, ToolingArtifactManager.MULE_PLUGIN);
    schemaInformation.ifPresent((info) -> {
      moduleSchemas.put(info.getNamespace(), info);
      moduleSchemas.put(info.getSchemaLocation(), info);
    });
  }

  //  private void loadMuleSchemasInClasspath() {
  //    final Map<String, String> schemaUrlsAndFileNames = getSchemasFromSpringSchemas(myModule);
  //    for (String schemaLocation: schemaUrlsAndFileNames.keySet()) {
  //      final String fileName = schemaUrlsAndFileNames.get(schemaLocation);
  //      final String relativePath = fileName.startsWith("/") ? fileName : "/" + fileName;
  //      final Set<FileType> fileTypes = Collections.singleton(FileTypeManager.getInstance().getFileTypeByFileName(relativePath));
  //      final List<VirtualFile> fileList = new ArrayList<>();
  //
  //      FileBasedIndex.getInstance().processFilesContainingAllKeys(FileTypeIndex.NAME, fileTypes, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule), null, virtualFile -> {
  //        if (virtualFile.getPath().endsWith(relativePath)) {
  //          fileList.add(virtualFile);
  //        }
  //        return true;
  //      });
  //      if (!fileList.isEmpty()) {
  //        final VirtualFile virtualFile = fileList.get(0);
  //        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
  //        if (psiFile instanceof XmlFile) {
  //          final XmlFile xmlFile = (XmlFile) psiFile;
  //          final XmlDocument document = xmlFile.getDocument();
  //          String defaultNamespace = schemaLocation;
  //          if (document != null) {
  //            final PsiMetaData metaData = document.getMetaData();
  //            if (metaData instanceof XmlNSDescriptorImpl) {
  //              XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
  //              defaultNamespace = descriptor.getDefaultNamespace();
  //            }
  //          }
  //          final String[] parts = defaultNamespace.split("/");
  //          final SchemaInformation schemaInformation = new SchemaInformation(virtualFile, defaultNamespace, schemaLocation, parts[parts.length - 1]);
  //          MuleSchemaRepository.getInstance(getMuleVersion()).addInternalSchema(schemaInformation);
  //          this.moduleSchemas.put(defaultNamespace, schemaInformation);
  //          this.moduleSchemas.put(schemaLocation, schemaInformation);
  //        }
  //      }
  //    }
  //  }


  private Map<String, String> parseSpringSchemas(String springSchemasContent) {
    Map<String, String> schemaUrlsAndFileNames = new HashMap<>();
    for (String line: springSchemasContent.split("\n")) {
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

//  private Map<String, String> getSchemasFromSpringSchemas(@NotNull Module module) {
//    return ReadAction.compute(() -> {
//      Map<String, String> schemasMap = new HashMap<>();
//      PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), MULE_SCHEMAS, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));
//
//      for (PsiFile nextSpringS: psiFiles) {
//        VirtualFile springSchemasFile = nextSpringS.getVirtualFile();
//        if (springSchemasFile != null) {
//          try {
//            String springSchemasContent = new String(springSchemasFile.contentsToByteArray(), springSchemasFile.getCharset());
//            schemasMap.putAll(parseSpringSchemas(springSchemasContent));
//          } catch (Exception e) {
//            e.printStackTrace();
//          }
//        }
//      }
//      return schemasMap;
//    });
//
//  }


  public static MuleModuleSchemaProvider getInstance(Module myModule) {
    return myModule.getComponent(MuleModuleSchemaProvider.class);
  }

  public Optional<SchemaInformation> getSchemaFromPrefix(String namespacePrefix) {
    initializeIfRequired();
    return this.moduleSchemas.values().stream().filter((schemaInfo) -> schemaInfo.getPrefix().equalsIgnoreCase(namespacePrefix)).findFirst();
  }

  @Nullable
  public String getPrefixFor(String namespace) {
    Optional<SchemaInformation> schema = getSchema(namespace);
    if (schema.isPresent()) {
      return schema.get().getPrefix();
    } else {
      String[] split = namespace.split("/");
      if (split.length > 0) {
        return split[split.length - 1];
      } else {
        return null;
      }
    }
  }
}
