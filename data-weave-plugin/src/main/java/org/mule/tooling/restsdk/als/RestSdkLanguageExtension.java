package org.mule.tooling.restsdk.als;

import amf.core.client.common.remote.Content;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.commons.io.IOUtils;
import org.mule.tooling.als.component.ALSLanguageExtension;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestSdkLanguageExtension implements ALSLanguageExtension {

  public static final String REST_SDK_DIALECT_YAML_PATH = "com/mulesoft/connectivity/rest/aml/dialects/rest_sdk_dialect.yaml";
  public static final String REST_SDK_DIALECT_URL = "file:///" + REST_SDK_DIALECT_YAML_PATH;

  public static final String REST_SDK_VOCABULARY_YAML_PATH = "com/mulesoft/connectivity/rest/aml/vocabularies/rest_sdk_vocabulary.yaml";
  public static final String REST_SDK_VOCABULARY_URL = "file:///" + REST_SDK_VOCABULARY_YAML_PATH;


  @Override
  public boolean supports(PsiFile file) {
    return RestSdkHelper.isRestSdkDescriptorFile(file);
  }

  @Override
  public Optional<Dialect> customDialect(Project project) {
    final Map<String, Content> resources = new HashMap<>();
    try {
      contentFrom(REST_SDK_DIALECT_YAML_PATH).ifPresent((c) -> resources.put(REST_SDK_DIALECT_URL, c));
      contentFrom(REST_SDK_VOCABULARY_YAML_PATH).ifPresent((c) -> resources.put(REST_SDK_VOCABULARY_URL, c));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(new Dialect(REST_SDK_DIALECT_URL, resources));
  }


  private Optional<Content> contentFrom(String path) throws IOException {
    URL resourceUrl = getClass().getClassLoader().getResource(path);
    if (resourceUrl != null) {
      String content = IOUtils.toString(resourceUrl, StandardCharsets.UTF_8);
      return Optional.of(new Content(content, resourceUrl.toExternalForm(), "application/yaml"));
    }
    return Optional.empty();
  }
}
