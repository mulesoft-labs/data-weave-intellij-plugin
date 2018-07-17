package org.mule.tooling.runtime.schema;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.client.api.descriptors.ArtifactDescriptor;
import org.mule.tooling.client.api.extension.ExtensionModelService;
import org.mule.tooling.client.api.extension.model.ExtensionModel;
import org.mule.tooling.client.api.extension.model.XmlDslModel;
import org.mule.tooling.runtime.tooling.ToolingRuntimeManager;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MuleSchemaRepository {

  public static final String ORG_MULE_RUNTIME = "org.mule.runtime";
  public static final String ORG_SPRINGFRAMEWORK = "org.springframework";
  public static final String ORG_MULE_TOOLING = "org.mule.tooling";
  public static final String COM_MULESOFT_RUNTIME = "com.mulesoft.runtime";
  public static final String COM_MULESOFT_MUNIT = "com.mulesoft.munit";

  public static final String MULE_CORE_ARTIFACT_ID = "mule-core";
  public static final String MULE_ARTIFACT_ID = "mule";
  public static final String MODULE_ARTIFACT_ID = "module";
  public static final String DOMAIN_ARTIFACT_ID = "domain";
  public static final String SPRING_BEANS_ARTIFACT_ID = "spring-beans";
  public static final String MULE_SCHEMADOC_ARTIFACT_ID = "mule-schemadoc";
  public static final String DOCUMENTATION_ARTIFACT_ID = "documentation";
  public static final String MULE_EE_ARTIFACT_ID = "mule-ee";
  public static final String MULE_MODULE_TLS_ARTIFACT_ID = "mule-module-tls";
  public static final String MULE_MODULE_BATCH = "mule-module-batch";
  public static final String MUNIT_TOOLS = "munit-tools";
  public static final String MUNIT_RUNNER = "munit-runner";

  private static final String BATCH_NS = "http://www.mulesoft.org/schema/mule/batch";
  private static final String BATCH_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/batch/current/mule-batch.xsd";

  private static final String TLS_NS = "http://www.mulesoft.org/schema/mule/tls";
  private static final String TLS_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/tls/current/mule-tls.xsd";

  private static final String MUNIT_NS = "http://www.mulesoft.org/schema/mule/munit";
  private static final String MUNIT_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/munit/current/mule-munit.xsd";

  private static String MULE_EE_NS = "http://www.mulesoft.org/schema/mule/ee/core";
  private static String MULE_EE_SCHEMA_LOCATION = "http://www.mulesoft.org/schema/mule/ee/core/current/mule-ee.xsd";

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


  private static final String SCHEMA_LOCATION_PROP = "schemaLocation";
  private static final String NAMESPACE_PROP = "namespace";
  private static final String PREFIX_PROP = "prefix";

  private final static ConcurrentHashMap<String, MuleSchemaRepository> schemaManagersByRuntimeVersion = new ConcurrentHashMap<>();

  public static MuleSchemaRepository getInstance(String runtimeVersion) {
    String key = runtimeVersion;
    MuleSchemaRepository muleSchemaRepository = schemaManagersByRuntimeVersion.get(key);
    if (muleSchemaRepository == null) {
      synchronized (schemaManagersByRuntimeVersion) {
        if (schemaManagersByRuntimeVersion.containsKey(key)) {
          return schemaManagersByRuntimeVersion.get(key);
        } else {
          final MuleSchemaRepository value = new MuleSchemaRepository(runtimeVersion);
          schemaManagersByRuntimeVersion.put(key, value);
          return value;
        }
      }
    } else {
      return muleSchemaRepository;
    }
  }

  private String runtimeVersion;

  private Map<SchemaCoordinate, SchemaInformation> schemas;
  private List<SchemaInformation> internalSchemas;

  private MuleSchemaRepository(String runtimeVersion) {
    this.runtimeVersion = runtimeVersion;

    this.schemas = new HashMap<>();
    this.internalSchemas = new ArrayList<>();
    initComponent();
  }

  public void addInternalSchema(SchemaInformation information) {
    this.internalSchemas.add(information);
  }

  private void initComponent() {
    initSchemasForRuntime();
    loadLocalComponents();
  }

  private void loadLocalComponents() {
    final File schemaDirectory = MuleDirectoriesUtils.getRuntimeSchemaDirectory(runtimeVersion);
    if (schemaDirectory.exists()) {
      final File[] groupIds = schemaDirectory.listFiles();
      if (groupIds != null) {
        for (File groupId: groupIds) {
          final String groupIdName = groupId.getName();
          final File[] artifacts = groupId.listFiles();
          if (artifacts != null) {
            for (File artifact: artifacts) {
              final String artifactName = artifact.getName();
              final File[] versions = artifact.listFiles();
              if (versions != null) {
                for (File version: versions) {
                  final String versionName = version.getName();
                  loadSchema(groupIdName, artifactName, versionName, version);
                }
              }
            }
          }
        }
      }
    }
  }

  private Optional<SchemaInformation> loadSchema(String groupIdName, String artifactName, String versionName, File version) {
    final File xmlSchema = new File(version, getSchemaFileName(artifactName));
    final File propertiesInfo = new File(version, getPropertiesFileName(artifactName));
    if (xmlSchema.exists() && xmlSchema.isFile() && propertiesInfo.exists() && propertiesInfo.isFile()) {
      VirtualFile xmlVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(xmlSchema);
      Properties properties = new Properties();
      try {
        properties.load(new FileReader(propertiesInfo));
      } catch (IOException e) {
        e.printStackTrace();
        //We already checked that exists and that is a file so it should be all ok
      }
      final String schemaLocation = properties.getProperty(SCHEMA_LOCATION_PROP);
      final String namespaceProp = properties.getProperty(NAMESPACE_PROP);
      final String prefix = properties.getProperty(PREFIX_PROP);
      SchemaInformation schemaInformation = new SchemaInformation(xmlVirtualFile, namespaceProp, schemaLocation, prefix);
      schemas.put(new SchemaCoordinate(groupIdName, artifactName, versionName), schemaInformation);
      return Optional.of(schemaInformation);
    } else {
      return Optional.empty();
    }
  }

  @NotNull
  private String getPropertiesFileName(String artifactName) {
    return artifactName + ".properties";
  }

  @NotNull
  private static String getSchemaFileName(String artifactName) {
    return artifactName + ".xsd";
  }

  private void initSchemasForRuntime() {
    loadResourceBasedSchema(ORG_MULE_RUNTIME, MULE_CORE_ARTIFACT_ID, runtimeVersion, "schemas/mule-core.xsd", MULE_NS, MULE_CORE_SCHEMA_LOCATION, "mule");

    loadResourceBasedSchema(ORG_MULE_RUNTIME, MULE_ARTIFACT_ID, runtimeVersion, "schemas/mule-core.xsd", MULE_NS, MULE_SCHEMA_LOCATION, "mule");

    loadResourceBasedSchema(ORG_MULE_RUNTIME, MODULE_ARTIFACT_ID, runtimeVersion, "schemas/mule-module.xsd", MODULE_NS, MODULE_SCHEMA_LOCATION, "module");

    loadResourceBasedSchema(ORG_MULE_RUNTIME, MULE_MODULE_TLS_ARTIFACT_ID, runtimeVersion, "schemas/mule-tls.xsd", TLS_NS, TLS_SCHEMA_LOCATION, "tls");

    loadResourceBasedSchema(ORG_MULE_RUNTIME, DOMAIN_ARTIFACT_ID, runtimeVersion, "schemas/mule-domain.xsd", DOMAIN_NS, DOMAIN_SCHEMA_LOCATION, "domain");

    loadResourceBasedSchema(ORG_SPRINGFRAMEWORK, SPRING_BEANS_ARTIFACT_ID, runtimeVersion, "schemas/spring-beans-3.0.xsd", SPRING_BEANS_NS, SPRING_BEANS_SCEMA_LOCATION, "spring");

    loadResourceBasedSchema(ORG_MULE_RUNTIME, MULE_SCHEMADOC_ARTIFACT_ID, runtimeVersion, "schemas/mule-schemadoc.xsd", MULE_SCHEMA_DOC_NS, MULE_SCHEMA_DOC_SCHEMA_LOCATION, "sdoc");

    loadResourceBasedSchema(ORG_MULE_TOOLING, DOCUMENTATION_ARTIFACT_ID, runtimeVersion, "schemas/mule-documentation.xsd", DOCUMENTATION_NS, DOCUMENTATION_SCHEMA_LOCATION, "doc");

    loadResourceBasedSchema(COM_MULESOFT_RUNTIME, MULE_EE_ARTIFACT_ID, runtimeVersion, "schemas/mule-ee.xsd", MULE_EE_NS, MULE_EE_SCHEMA_LOCATION, "mule-ee");

    loadResourceBasedSchema(COM_MULESOFT_RUNTIME, MULE_MODULE_BATCH, runtimeVersion, "schemas/mule-batch.xsd", BATCH_NS, BATCH_SCHEMA_LOCATION, "batch");


  }

  private void loadResourceBasedSchema(String groupId, String artifactId, String version, String resource, String namespace, String schemaLocation, String prefix) {
    try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      if (resourceAsStream != null) {
        String content = IOUtils.toString(resourceAsStream, "UTF-8");
        addSchema(groupId, artifactId, version, content, namespace, schemaLocation, prefix);
      } else {
        Notifications.Bus.notify(new Notification("Schema Repository", "Unable fetch schema", "Unable to resolve schema " + resource, NotificationType.WARNING));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Optional<SchemaInformation> addSchema(String groupId, String artifactId, String version, String content, String namespace, String schemaLocation, String prefix) {
    final File schemaDirectory = MuleDirectoriesUtils.getRuntimeSchemaDirectory(runtimeVersion);
    final File elementFolder = new File(new File(new File(schemaDirectory, groupId), artifactId), version);
    if (!elementFolder.exists()) {
      elementFolder.mkdirs();
    }
    final File schemaFile = new File(elementFolder, getSchemaFileName(artifactId));
    try (FileWriter fileWriter = new FileWriter(schemaFile)) {
      IOUtils.write(content, fileWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
    final File propertiesFile = new File(elementFolder, getPropertiesFileName(artifactId));
    try (FileWriter fileWriter = new FileWriter(propertiesFile)) {
      Properties properties = new Properties();
      properties.put(SCHEMA_LOCATION_PROP, schemaLocation);
      properties.put(NAMESPACE_PROP, namespace);
      if (prefix != null) {
        properties.put(PREFIX_PROP, prefix);
      }
      properties.store(fileWriter, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //We load the entry that we just created
    return loadSchema(groupId, artifactId, version, elementFolder);
  }

  public Optional<SchemaInformation> searchSchemaByUrl(String url) {
    Collection<SchemaInformation> schemaInformations = schemas.values();
    Optional<SchemaInformation> schemaInformation = searchIn(url, schemaInformations);
    if (schemaInformation.isPresent()) {
      return schemaInformation;
    } else {
      return searchIn(url, internalSchemas);
    }
  }

  private Optional<SchemaInformation> searchIn(String url, Collection<SchemaInformation> schemaInformations) {
    return schemaInformations.stream()
        .filter(
            (schemaInformation ->
                schemaInformation.getNamespace().equalsIgnoreCase(url) || schemaInformation.getSchemaLocation().equalsIgnoreCase(url))
        )
        .findFirst();
  }

  public Optional<SchemaInformation> loadSchemaFromCoordinate(Project project, String groupId, String artifactId, String version, String classifier) {
    SchemaCoordinate schemaCoordinate = new SchemaCoordinate(groupId, artifactId, version);

    if (schemas.containsKey(schemaCoordinate)) {
      return Optional.ofNullable(schemas.get(schemaCoordinate));
    } else if (groupId.equals(COM_MULESOFT_MUNIT) && artifactId.equals(MUNIT_TOOLS)) {
      loadResourceBasedSchema(COM_MULESOFT_MUNIT, MUNIT_RUNNER, version, "schemas/mule-munit.xsd", MUNIT_NS, MUNIT_SCHEMA_LOCATION, "munit");
      return Optional.ofNullable(schemas.get(schemaCoordinate));
    } else {
      return ToolingRuntimeManager.getInstance().callOnToolingRuntime(project, runtimeVersion, (toolingRuntimeClient) -> {
        try {
          final ArtifactDescriptor artifactDescriptor = ArtifactDescriptor.newBuilder().withGroupId(groupId).withArtifactId(artifactId).withVersion(version).withClassifier(classifier).build();
          final ExtensionModelService extensionModelService = toolingRuntimeClient.extensionModelService();
          final Optional<String> extensionSchema = extensionModelService.loadExtensionSchema(artifactDescriptor);
          return extensionSchema.flatMap((schema) -> {
            final Optional<ExtensionModel> extensionModel = extensionModelService.loadExtensionModel(artifactDescriptor);
            final XmlDslModel xmlDslModel = extensionModel.get().getXmlDslModel();
            final String namespace = xmlDslModel.getNamespace();
            final String schemaLocation = xmlDslModel.getSchemaLocation();
            final String prefix = xmlDslModel.getPrefix();
            return addSchema(groupId, artifactId, version, schema, namespace, schemaLocation, prefix);
          });
        } catch (Exception e) {
          Notifications.Bus.notify(new Notification("Schema resolution", "Unable to resolve XML schema", "Unable to resolve xml schema for element " + groupId + ":" + artifactId + ":" + version + " . Reason: \n" + e.getMessage(), NotificationType.WARNING));
          return Optional.empty();
        }
      }, Optional::empty);
    }
  }


  public static class SchemaCoordinate {
    private String artifactId;
    private String groupId;
    private String version;

    public SchemaCoordinate(String groupId, String artifactId, String version) {
      this.artifactId = artifactId;
      this.groupId = groupId;
      this.version = version;
    }

    public String getArtifactId() {
      return artifactId;
    }

    public String getGroupId() {
      return groupId;
    }

    public String getVersion() {
      return version;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SchemaCoordinate that = (SchemaCoordinate) o;
      return Objects.equals(artifactId, that.artifactId) &&
          Objects.equals(groupId, that.groupId) &&
          Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
      return Objects.hash(artifactId, groupId, version);
    }

    @Override
    public String toString() {
      return "SchemaCoordinate{" +
          "artifactId='" + artifactId + '\'' +
          ", groupId='" + groupId + '\'' +
          ", version='" + version + '\'' +
          '}';
    }
  }
}
