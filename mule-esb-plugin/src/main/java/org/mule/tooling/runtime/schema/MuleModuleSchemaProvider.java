package org.mule.tooling.runtime.schema;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.mule.tooling.runtime.tooling.MuleRuntimeServerManager;
import org.mule.tooling.runtime.tooling.ToolingArtifactManager;
import org.mule.tooling.runtime.tooling.ToolingRuntimeListener;
import org.mule.tooling.runtime.tooling.ToolingRuntimeTopics;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_MULE_RUNTIME;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_MULE_TOOLING;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.ORG_SPRINGFRAMEWORK;
import static org.mule.tooling.runtime.schema.MuleSchemaRepository.SPRING_BEANS_ARTIFACT_ID;

/**
 * Handles schema for a given module. It delegates on the MuleSchemaRepository
 * It will use the module dependencies to resolve visibilty of what schemas are reachable and not.
 */
public class MuleModuleSchemaProvider implements ModuleComponent {

  public static final String MULE_SCHEMAS = "mule.schemas";

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

  private void initializeIfRequired() {
    if (!initialized && !initializing) {
      initializing = true;
      loadDefaultSchemas();

      loadMuleSchemasInClasspath();
      loadSchemasFromTooling();
      //Listen to classpath changes
      project.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
        @Override
        public void rootsChanged(ModuleRootEvent event) {
          moduleSchemas.clear();
          loadMuleSchemasInClasspath();
          loadSchemasFromTooling();
        }
      });
      //Listen to tooling runtime being loaded
      ApplicationManager.getApplication().getMessageBus().connect().subscribe(ToolingRuntimeTopics.TOOLING_STARTED, new ToolingRuntimeListener() {
        @Override
        public void onToolingRuntimeStarted(String muleVersion) {
          loadSchemasFromTooling();
        }
      });
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
  }

  private void loadSchema(String groupId, String artifactId, String version) {
    Optional<SchemaInformation> schemaInformation = MuleSchemaRepository.getInstance(getMuleVersion()).loadSchemaFromCoordinate(myModule.getProject(), groupId, artifactId, version, ToolingArtifactManager.MULE_PLUGIN);
    schemaInformation.ifPresent((info) -> {
      moduleSchemas.put(info.getNamespace(), info);
      moduleSchemas.put(info.getSchemaLocation(), info);
    });
  }

  private void loadMuleSchemasInClasspath() {
    final Map<String, String> schemaUrlsAndFileNames = getSchemasFromSpringSchemas(myModule);
    for (String schemaLocation: schemaUrlsAndFileNames.keySet()) {
      final String fileName = schemaUrlsAndFileNames.get(schemaLocation);
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
          String defaultNamespace = schemaLocation;
          if (document != null) {
            final PsiMetaData metaData = document.getMetaData();
            if (metaData instanceof XmlNSDescriptorImpl) {
              XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
              defaultNamespace = descriptor.getDefaultNamespace();
            }
          }
          final String[] parts = defaultNamespace.split("/");
          final SchemaInformation schemaInformation = new SchemaInformation(virtualFile, defaultNamespace, schemaLocation, parts[parts.length - 1]);
          MuleSchemaRepository.getInstance(getMuleVersion()).addInternalSchema(schemaInformation);
          this.moduleSchemas.put(defaultNamespace, schemaInformation);
          this.moduleSchemas.put(schemaLocation, schemaInformation);
        }
      }
    }
  }


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

  private Map<String, String> getSchemasFromSpringSchemas(@NotNull Module module) {
    return ReadAction.compute(() -> {
      Map<String, String> schemasMap = new HashMap<>();
      PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), MULE_SCHEMAS, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));

      for (PsiFile nextSpringS: psiFiles) {
        VirtualFile springSchemasFile = nextSpringS.getVirtualFile();
        if (springSchemasFile != null) {
          try {
            String springSchemasContent = new String(springSchemasFile.contentsToByteArray(), springSchemasFile.getCharset());
            schemasMap.putAll(parseSpringSchemas(springSchemasContent));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      return schemasMap;
    });

  }


  public static MuleModuleSchemaProvider getInstance(Module myModule) {
    return myModule.getComponent(MuleModuleSchemaProvider.class);
  }
}
