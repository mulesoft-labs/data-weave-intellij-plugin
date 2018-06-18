package org.mule.tooling.runtime.schema;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;

import java.util.Optional;

public class SchemaInformation {

  private VirtualFile xsdFile;
  private String namespace;
  private String schemaLocation;
  private String prefix;


  public SchemaInformation(VirtualFile xsdFile, String namespace, String schemaLocation, String prefix) {
    this.xsdFile = xsdFile;
    this.namespace = namespace;
    this.schemaLocation = schemaLocation;
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public VirtualFile getXsdFile() {
    return xsdFile;
  }

  public Optional<XmlFile> getSchemaAsXmlFile(Project project) {
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(xsdFile);
    if (psiFile instanceof XmlFile) {
      return Optional.of((XmlFile) psiFile);
    }
    return Optional.empty();
  }

  public String getNamespace() {
    return namespace;
  }

  public String getSchemaLocation() {
    return schemaLocation;
  }
}
