package org.mule.tooling.als.languages;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLFileType;
import org.mule.tooling.als.component.ALSLanguageExtension;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.Objects;
import java.util.Optional;

public class WebApiLanguage implements ALSLanguageExtension {

  public static SelectionPath swaggerVersion = SelectionPath.DOCUMENT.child("swagger");
  public static SelectionPath openApiVersion = SelectionPath.DOCUMENT.child("openapi");
  public static SelectionPath asyncApiVersion = SelectionPath.DOCUMENT.child("asyncapi");

  @Override
  public boolean supports(PsiFile psiFile) {
    if (psiFile.getFileType() instanceof YAMLFileType) {
      return swaggerVersion.selectYaml(psiFile) != null ||
              openApiVersion.selectYaml(psiFile) != null ||
              asyncApiVersion.selectYaml(psiFile) != null ||
              Objects.equals(psiFile.getOriginalFile().getVirtualFile().getExtension(), "raml");

    } else if (psiFile.getFileType() instanceof JsonFileType) {
      return swaggerVersion.selectJson(psiFile) != null ||
              openApiVersion.selectJson(psiFile) != null ||
              asyncApiVersion.selectJson(psiFile) != null;
    } else {
      return false;
    }
  }

  @Override
  public Optional<Dialect> customDialect(Project project) {
    return Optional.empty();
  }
}
