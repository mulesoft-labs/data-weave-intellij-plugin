package org.mule.tooling.als.component;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLFileType;
import org.mule.tooling.als.utils.LSPUtils;

import java.util.HashMap;
import java.util.Optional;

public class UserDialectLanguageExtension implements ALSLanguageExtension {

  private String header;
  private String filePath;

  public UserDialectLanguageExtension(String name, String filePath) {
    this.filePath = filePath;
    this.header = name;
  }

  @Override
  public boolean supports(PsiFile file) {
    return isDialectDescriptor(file);
  }

  @Override
  public Optional<Dialect> customDialect(Project project) {
    final String fileUrl = LSPUtils.toLSPUrl(filePath);
    return Optional.of(new Dialect(fileUrl, new HashMap<>()));
  }

  public boolean isDialectDescriptor(PsiFile containingFile) {
    if (containingFile.getFileType() instanceof YAMLFileType) {
      String text = containingFile.getText();
      return isDialectDescriptor(text);
    } else {
      return false;
    }
  }

  public boolean isDialectDescriptor(String text) {
    return text.contains("#%" + header);
  }
}
