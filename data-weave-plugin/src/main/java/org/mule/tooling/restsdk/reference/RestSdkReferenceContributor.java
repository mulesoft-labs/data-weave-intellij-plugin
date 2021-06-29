package org.mule.tooling.restsdk.reference;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RestSdkReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(sampleDataRef(), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{new LocalReference(element, "sampleData")};
            }
        });
        registrar.registerReferenceProvider(paginationRef(), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{new LocalReference(element, "paginations")};
            }
        });

        registrar.registerReferenceProvider(valueProviderRef(), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{new LocalReference(element, "valueProviders")};
            }
        });
    }

    private PsiElementPattern.Capture<YAMLScalar> sampleDataRef() {
        return psiElement(YAMLScalar.class)
                .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("id")))
                .and(psiElement().withSuperParent(3, psiElement(YAMLKeyValue.class).withName("sampleData")))
                .withLanguage(YAMLLanguage.INSTANCE);
    }

    private PsiElementPattern.Capture<YAMLScalar> valueProviderRef() {
        return psiElement(YAMLScalar.class)
                .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("id")))
                .and(psiElement().withSuperParent(3, psiElement(YAMLKeyValue.class).withName("valueProvider")))
                .withLanguage(YAMLLanguage.INSTANCE);
    }

    private PsiElementPattern.Capture<YAMLScalar> paginationRef() {
        return psiElement(YAMLScalar.class)
                .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("pagination")))
                .withLanguage(YAMLLanguage.INSTANCE);
    }


}
