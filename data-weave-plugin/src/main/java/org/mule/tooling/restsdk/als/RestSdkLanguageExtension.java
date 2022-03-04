package org.mule.tooling.restsdk.als;

import amf.core.client.common.remote.Content;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.als.component.ALSLanguageExtension;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class RestSdkLanguageExtension implements ALSLanguageExtension {

  public static final String REST_SDK_DIALECT_YAML_FILE_NAME = "rest_sdk_dialect.yaml";
  public static final String REST_SDK_DIALECT_YAML_PATH = "com/mulesoft/connectivity/rest/aml/dialects/" + REST_SDK_DIALECT_YAML_FILE_NAME;
  public static final String REST_SDK_DIALECT_URL = "file:///" + REST_SDK_DIALECT_YAML_PATH;

  public static final String REST_SDK_VOCABULARY_YAML_FILE_NAME = "rest_sdk_vocabulary.yaml";
  public static final String REST_SDK_VOCABULARY_YAML_PATH = "com/mulesoft/connectivity/rest/aml/vocabularies/" + REST_SDK_VOCABULARY_YAML_FILE_NAME;
  public static final String REST_SDK_VOCABULARY_URL = "file:///" + REST_SDK_VOCABULARY_YAML_PATH;


  @Override
  public boolean supports(PsiFile file) {
    return RestSdkHelper.isRestSdkDescriptorFile(file);
  }

  @Override
  public Optional<Dialect> customDialect(Project project) {
    final Map<String, Content> resources = new HashMap<>();
    final AtomicReference<String> dialectUrl = new AtomicReference<>(REST_SDK_DIALECT_URL);
    final Stream<VirtualFile> dialect = FilenameIndex.getVirtualFilesByName(project, REST_SDK_DIALECT_YAML_FILE_NAME, GlobalSearchScope.allScope(project)).stream();
    dialect.findFirst()
            .ifPresentOrElse((vf) -> {
              //If vf is present in the project dependencies we take that
              contentFrom(vf).ifPresent((c) -> {
                dialectUrl.set(vf.getUrl());
                resources.put(vf.getUrl(), c);
              });
            }, () -> {
              contentFrom(REST_SDK_DIALECT_YAML_PATH)
                      .ifPresent((c) -> resources.put(REST_SDK_DIALECT_URL, c));
            });

    final Stream<VirtualFile> vocabulary = FilenameIndex.getVirtualFilesByName(project, REST_SDK_VOCABULARY_YAML_FILE_NAME, GlobalSearchScope.allScope(project)).stream();
    vocabulary.findFirst()
            .ifPresentOrElse((vf) -> {
              contentFrom(vf)
                      .ifPresent((c) -> resources.put(vf.getUrl(), c));
            }, () -> {
              contentFrom(REST_SDK_VOCABULARY_YAML_PATH)
                      .ifPresent((c) -> resources.put(REST_SDK_VOCABULARY_URL, c));
            });

    return Optional.of(new Dialect(dialectUrl.get(), resources));
  }

  private Optional<Content> contentFrom(String path) {
    URL resourceUrl = getClass().getClassLoader().getResource(path);
    if (resourceUrl != null) {
      String content;
      try {
        content = IOUtils.toString(resourceUrl, StandardCharsets.UTF_8);
      } catch (IOException e) {
        return Optional.empty();
      }
      return Optional.of(new Content(content, resourceUrl.toExternalForm(), "application/yaml"));
    }
    return Optional.empty();
  }

  private Optional<Content> contentFrom(@NotNull VirtualFile path) {
    String content;
    try {
      content = IOUtils.toString(path.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      return Optional.empty();
    }
    return Optional.of(new Content(content, path.getUrl(), "application/yaml"));

  }
}
