package org.mule.tooling.restsdk.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;

public class FilePathReferenceProvider extends PsiReferenceProvider {

  private final boolean myEndingSlashNotAllowed;

  public FilePathReferenceProvider() {
    this(true);
  }

  public FilePathReferenceProvider(boolean endingSlashNotAllowed) {
    myEndingSlashNotAllowed = endingSlashNotAllowed;
  }

  @NotNull
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                               String text,
                                               int offset) {
    return new FileReferenceSet(text, element, offset, this, true, myEndingSlashNotAllowed).getAllReferences();
  }

  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (element instanceof YAMLScalar) {
      String fileName = ((YAMLScalar) element).getTextValue();
      return getReferencesByElement(element, fileName, element.getText().length() - fileName.length());
    } else {
      return new PsiReference[0];
    }
  }
}