package org.mule.tooling.lang.dw.testintegration;

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
import org.mule.tooling.lang.dw.WeaveFile;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;
import org.mule.tooling.lang.dw.util.WeaveUtils;

import javax.swing.*;

public class WeaveTestFramework implements TestFramework {


    @NotNull
    @Override
    public String getName() {
        return "WeaveTest";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return WeaveIcons.DataWeaveIcon;
    }

    @Override
    public boolean isLibraryAttached(@NotNull Module module) {
        VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl("dw/test/Test.dwl");
        return fileByUrl != null;
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
            return WeaveUtils.isTestFile(document);
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
