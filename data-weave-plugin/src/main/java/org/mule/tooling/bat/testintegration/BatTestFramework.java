package org.mule.tooling.bat.testintegration;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.lang.Language;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.testIntegration.TestFramework;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.bat.BatIcons;
import org.mule.tooling.bat.utils.BatUtils;
import org.mule.tooling.lang.dw.WeaveFile;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatTestFramework implements TestFramework {

  @NotNull
  @Override
  public String getName() {
    return "BatTest";
  }

  public final static List<String> BAT_YAML_FILES = Arrays.asList("bat.yaml", "bat.yml");

  @NotNull
  @Override
  public Icon getIcon() {
    return BatIcons.WeaveFileType;
  }

  @Override
  public boolean isLibraryAttached(@NotNull Module module) {
    VirtualFile bddUrl = VirtualFileManager.getInstance().findFileByUrl("bat/BDD.dwl");
    VirtualFile coreUrl = VirtualFileManager.getInstance().findFileByUrl("bat/Core.dwl");
    return bddUrl != null || coreUrl != null;
  }

  @Nullable
  @Override
  public String getLibraryPath() {
    return null;
  }

  @Nullable
  @Override
  public String getDefaultSuperClass() {
    return null;
  }

  @Override
  public boolean isTestClass(@NotNull PsiElement psiElement) {
    if (psiElement instanceof WeaveFile) {
      WeaveDocument document = ((WeaveFile) psiElement).getDocument();
      return BatUtils.isTestFile(document) ||  BAT_YAML_FILES.contains(document.getName());
    }
    return false;
  }

  @Override
  public boolean isPotentialTestClass(@NotNull PsiElement psiElement) {
    return false;
  }

  @Nullable
  @Override
  public PsiElement findSetUpMethod(@NotNull PsiElement psiElement) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement findTearDownMethod(@NotNull PsiElement psiElement) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement findOrCreateSetUpMethod(@NotNull PsiElement psiElement) throws IncorrectOperationException {
    return null;
  }

  @Override
  public FileTemplateDescriptor getSetUpMethodFileTemplateDescriptor() {
    return null;
  }

  @Override
  public FileTemplateDescriptor getTearDownMethodFileTemplateDescriptor() {
    return null;
  }

  @Override
  public FileTemplateDescriptor getTestMethodFileTemplateDescriptor() {
    return null;
  }

  @Override
  public boolean isIgnoredMethod(PsiElement psiElement) {
    return false;
  }

  @Override
  public boolean isTestMethod(PsiElement psiElement) {
    return false;
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return WeaveLanguage.getInstance();
  }
}
