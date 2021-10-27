package org.mule.tooling.als.component;


import amf.core.client.common.remote.Content;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Map;
import java.util.Optional;

/**
 * Contributes with new language supports for ALS
 */
public interface ALSLanguageExtension {
  /**
   * Return true if this file is being supported by this language
   *
   * @param file The file
   * @return true if supported
   */
  boolean supports(PsiFile file);

  /**
   * Any additional resource that this language requires. For example any Dialect
   *
   * @return a map where the key is the file path
   */
  Optional<Dialect> customDialect(Project project);

  class Dialect {
    private String dialectUrl;
    private Map<String, Content> resources;

    public Dialect(String dialectUrl, Map<String, Content> resources) {
      this.dialectUrl = dialectUrl;
      this.resources = resources;
    }

    public String getDialectUrl() {
      return dialectUrl;
    }

    public Map<String, Content> getResources() {
      return resources;
    }
  }
}
