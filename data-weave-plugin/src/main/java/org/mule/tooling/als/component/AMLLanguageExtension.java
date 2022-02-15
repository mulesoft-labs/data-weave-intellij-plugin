package org.mule.tooling.als.component;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Optional;

public class AMLLanguageExtension implements ALSLanguageExtension{
  @Override
  public boolean supports(PsiFile file) {
    return isDialectDescriptor(file);
  }

  @Override
  public Optional<Dialect> customDialect(Project project) {
    return Optional.empty();
  }


  public static boolean isDialectDescriptor(PsiFile containingFile) {
    if (containingFile.getFileType() instanceof YAMLFileType) {
      String text = containingFile.getText();
      return isDialectDescriptor(text);
    } else {
      return false;
    }
  }

  public static boolean isDialectDescriptor(String text) {
    return text.contains("#%Dialect 1.0") || text.contains("#%Vocabulary 1.0") ;
  }
}
