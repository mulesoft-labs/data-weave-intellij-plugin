package org.mule.tooling.restsdk.reference;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RestSdkReferenceContributor extends PsiReferenceContributor {
  private static final JavaClassReferenceProvider NATIVE_OPERATIONS_REFERENCE_PROVIDER = new JavaClassReferenceProvider();
  static {
    NATIVE_OPERATIONS_REFERENCE_PROVIDER.setOption(JavaClassReferenceProvider.INSTANTIATABLE, true);
  }

  private static final JavaClassReferenceProvider VALUE_PROVIDERS_REFERENCE_PROVIDER = new JavaClassReferenceProvider();
  static {
    VALUE_PROVIDERS_REFERENCE_PROVIDER.setOption(JavaClassReferenceProvider.INSTANTIATABLE, true);
    VALUE_PROVIDERS_REFERENCE_PROVIDER.setOption(JavaClassReferenceProvider.SUPER_CLASSES,
            List.of("org.mule.runtime.extension.api.values.ValueProvider"));
  }

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

    registrar.registerReferenceProvider(apiRef(), new FilePathReferenceProvider());

    registrar.registerReferenceProvider(path(), new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[]{new ApiPathReference(element, ((YAMLScalar) element).getTextValue())};
      }
    });

    registrar.registerReferenceProvider(endpointOperationPath(), new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        final ApiPathReference reference;
        if (element instanceof  YAMLKeyValue) {
          YAMLKeyValue kv = (YAMLKeyValue) element;
          reference = new ApiPathReference(element, kv.getKeyText());
          assert kv.getKey() != null;
          reference.setRangeInElement(kv.getKey().getTextRangeInParent());
        } else {
          reference = new ApiPathReference(element, ((YAMLScalar)element).getTextValue());
        }
        return new PsiReference[]{reference};
      }
    });

    registrar.registerReferenceProvider(operationId(), new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[]{new OperationIDPathReference((YAMLScalar) element)};
      }
    });

    registrar.registerReferenceProvider(typeSchemaRef(), new FilePathReferenceProvider());

    registrar.registerReferenceProvider(outputTypeRef(), new FilePathReferenceProvider());

    registrar.registerReferenceProvider(operationFqnRef(), new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return NATIVE_OPERATIONS_REFERENCE_PROVIDER.getReferencesByElement(element);
      }
    });

    registrar.registerReferenceProvider(valueProviderFqnRef(), new PsiReferenceProvider() {
      @Override
      public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return VALUE_PROVIDERS_REFERENCE_PROVIDER.getReferencesByElement(element);
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

  private PsiElementPattern.Capture<YAMLScalar> apiRef() {
    return psiElement(YAMLScalar.class)
            .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("url")))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<YAMLScalar> path() {
    return psiElement(YAMLScalar.class)
            .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("path")))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<?> endpointOperationPath() {
    /* It seems you can't match just a key, because the YAML plugin doesn't ask the "contributor"
     * machinery for references, so we must match the enclosing YAMLKeyValue.
     * We also match YAMLScalar to deal with a partially typed key-value that doesn't have a colon yet.
     */
    return psiElement()
            .withSuperParent(2, psiElement(YAMLKeyValue.class).withName("endpoints"))
            .andOr(psiElement(YAMLKeyValue.class), psiElement(YAMLScalar.class));
  }

  private PsiElementPattern.Capture<YAMLScalar> operationId() {
    return psiElement(YAMLScalar.class)
            .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("operationId")))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<YAMLScalar> typeSchemaRef() {
    return psiElement(YAMLScalar.class)
            .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("typeSchema")))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<YAMLScalar> outputTypeRef() {
    return psiElement(YAMLScalar.class)
            .and(psiElement().withParent(psiElement(YAMLKeyValue.class).withName("outputType")))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<YAMLScalar> operationFqnRef() {
    // matches VALUE in: operations | OPNAME | fqn | VALUE
    return psiElement(YAMLScalar.class)
            .withParent(psiElement(YAMLKeyValue.class).withName("fqn"))
            .withSuperParent(5, psiElement(YAMLKeyValue.class).withName("operations"))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

  private PsiElementPattern.Capture<YAMLScalar> valueProviderFqnRef() {
    // matches VALUE in: valueProvider | fqn | VALUE
    return psiElement(YAMLScalar.class)
            .withParent(psiElement(YAMLKeyValue.class).withName("fqn"))
            .withSuperParent(3, psiElement(YAMLKeyValue.class).withName("valueProvider"))
            .withLanguage(YAMLLanguage.INSTANCE);
  }

}
