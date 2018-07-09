package org.mule.tooling.runtime.munit;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mule.tooling.runtime.RuntimeIcons;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.testIntegration.TestFramework;
import com.intellij.util.IncorrectOperationException;

public class MUnitTestFramework implements TestFramework {

  @NotNull
  @Override
  public String getName() {
    return "MUnitTest";
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return RuntimeIcons.MUnitIcon;
  }

  @Override
  public boolean isLibraryAttached(@NotNull Module module) {
    return true;
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
    if (psiElement.getLanguage() instanceof XMLLanguage) {
      return psiElement.getContainingFile().getName().endsWith("test.xml");
    }
    return false;
  }

  @Override
  public boolean isPotentialTestClass(@NotNull PsiElement clazz) {
    return false;
  }

  @Nullable
  @Override
  public PsiElement findSetUpMethod(@NotNull PsiElement clazz) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement findTearDownMethod(@NotNull PsiElement clazz) {
    return null;
  }

  @Nullable
  @Override
  public PsiElement findOrCreateSetUpMethod(@NotNull PsiElement clazz) throws IncorrectOperationException {
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

  @NotNull
  @Override
  public FileTemplateDescriptor getTestMethodFileTemplateDescriptor() {
    return null;
  }

  @Override
  public boolean isIgnoredMethod(PsiElement element) {
    return false;
  }

  @Override
  public boolean isTestMethod(PsiElement element) {
    return false;
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return XMLLanguage.INSTANCE;
  }
}
