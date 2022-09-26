package org.mule.tooling.restsdk.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.impl.YAMLKeyValueImpl;
import org.mule.tooling.restsdk.utils.JavaUtils;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.util.text.Strings.capitalize;

public class RestSdkLineMarkerProvider extends RelatedItemLineMarkerProvider {

  public static final String DEFAULT_BASE_PACKAGE = "com.mulesoft.connectors.";
  public static final String INTERNAL_PACKAGE_NAME = "internal";
  public static final String OPERATION_SUFFIX = "Operation";
  public static final String TRIGGER_SUFFIX = "Trigger";
  public static final String OPERATION_PACKAGE_NAME = "operation";
  public static final String TRIGGER_PACKAGE_NAME = "source";

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    List<PsiClass> results = new ArrayList<>();
    Project myProject = element.getProject();
    if (element instanceof YAMLKeyValue) {
      final SelectionPath selectionPath = SelectionPath.pathOfYaml(element);
      final SelectionPath parent = selectionPath.getParent();
      if (parent != null) {
        switch (parent.getName()) {
          case RestSdkPaths.OPERATIONS:
          case RestSdkPaths.TRIGGERS: {
            final PsiFile containingFile = element.getContainingFile();
            boolean endpointOperation = parent.matches(RestSdkPaths.ENDPOINTS_METHOD_PATH);
            if (endpointOperation) {
              YAMLKeyValueImpl kv = ObjectUtils.tryCast(element.getParent().getParent().getParent().getParent(), YAMLKeyValueImpl.class);
              if (kv != null)
                collectEndpointOperationClasses(kv.getKeyText(), selectionPath.getName(), containingFile, results);
            } else {
              // Add the property to a collection of line marker info
              final PsiElement psiElement = RestSdkPaths.CONNECTOR_NAME_PATH.selectYaml(containingFile);
              if (psiElement != null && psiElement.getText() != null) {
                final String name = selectionPath.getName();

                String packageName;
                String operationClassName;
                if (parent.getName().equals(RestSdkPaths.OPERATIONS)) {
                  packageName = DEFAULT_BASE_PACKAGE + toValidPackageName(psiElement) + "." + INTERNAL_PACKAGE_NAME + "." + OPERATION_PACKAGE_NAME;
                  operationClassName = capitalize(name) + OPERATION_SUFFIX;
                } else {
                  packageName = DEFAULT_BASE_PACKAGE + toValidPackageName(psiElement) + "." + INTERNAL_PACKAGE_NAME + "." + TRIGGER_PACKAGE_NAME;
                  operationClassName = capitalize(name) + TRIGGER_SUFFIX;
                }

                final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(myProject);
                final PsiClass operationClass = javaPsiFacade.findClass(packageName + "." + operationClassName, ProjectScope.getProjectScope(myProject));
                if (operationClass != null) {
                  results.add(operationClass);
                }

                final PsiClass operationClassIntereceptor = javaPsiFacade.findClass(packageName + "." + "interceptor" + "." + operationClassName + "Interceptor", ProjectScope.getProjectScope(myProject));
                if (operationClassIntereceptor != null) {
                  results.add(operationClassIntereceptor);
                }

                final PsiClass operationClassBase = javaPsiFacade.findClass(packageName + "." + "base" + "." + operationClassName + "Base", ProjectScope.getProjectScope(myProject));
                if (operationClassBase != null) {
                  results.add(operationClassBase);
                }
              }
            }
            break;
          }
          case RestSdkPaths.SAMPLE_DATA: {
            final PsiFile containingFile = element.getContainingFile();
            final PsiElement psiElement = RestSdkPaths.CONNECTOR_NAME_PATH.selectYaml(containingFile);
            if (psiElement != null && psiElement.getText() != null) {
              final String name = selectionPath.getName();
              final String packageName = DEFAULT_BASE_PACKAGE + toValidPackageName(psiElement) + "." + INTERNAL_PACKAGE_NAME + "." + "metadata" + "." + "sample";
              final String sampleDataClassName = capitalize(name) + "SampleDataProvider";
              final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(myProject);
              final PsiClass operationClass = javaPsiFacade.findClass(packageName + "." + sampleDataClassName, ProjectScope.getProjectScope(myProject));
              if (operationClass != null) {
                results.add(operationClass);
              }
            }
            break;
          }
          case RestSdkPaths.VALUE_PROVIDERS: {
            final PsiFile containingFile = element.getContainingFile();
            final PsiElement psiElement = RestSdkPaths.CONNECTOR_NAME_PATH.selectYaml(containingFile);
            if (psiElement != null && psiElement.getText() != null) {
              final String name = selectionPath.getName();
              final String packageName = DEFAULT_BASE_PACKAGE + toValidPackageName(psiElement) + "." + INTERNAL_PACKAGE_NAME + "." + "metadata" + "." + "values";
              final String sampleDataClassName = capitalize(name) + "ExtensionsRestValueProvider";
              final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(myProject);
              final PsiClass operationClass = javaPsiFacade.findClass(packageName + "." + sampleDataClassName, ProjectScope.getProjectScope(myProject));
              if (operationClass != null) {
                results.add(operationClass);
              }
            }
            break;
          }
        }
      }

      if (!results.isEmpty()) {
        final NavigationGutterIconBuilder<PsiElement> builder =
                NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                        .setTargets(results)
                        .setTooltipText("Click to navigate to Generated Classes.");
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }

  /** Collects classes that define a given endpoint operation.
   *
   * <p>The function uses an heuristic that selects any class that:
   *
   * <ul>
   *  <li> has a method with <code>@OutputResolver</code> annotation
   *  <li> has an <code>OPERATION_PATH</code> field with the given path
   *  <li> its class name starts with the capitalized HTTP method
   * </ul>
   *
   * @param path        endpoint path
   * @param httpMethod  HTTP method of the operation
   * @param context     a {@link PsiElement} for context
   * @param classes     a collection to put the classes in
   */
  private static void collectEndpointOperationClasses(String path, String httpMethod, PsiElement context, Collection<PsiClass> classes) {
    var capitalizedMethod = capitalize(httpMethod);
    var psiMethods = CachedValuesManager.getCachedValue(context, () -> {
      var module = ModuleUtil.findModuleForPsiElement(context);
      final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(context.getProject());
      var outputResolverAnnotationClass = javaPsiFacade.findClass("org.mule.runtime.extension.api.annotation.metadata.OutputResolver", ProjectScope.getLibrariesScope(context.getProject()));
      if (outputResolverAnnotationClass == null || module == null)
        return null;
      Collection<PsiMethod> methodList = AnnotatedElementsSearch.searchPsiMethods(outputResolverAnnotationClass, module.getModuleWithLibrariesScope()).findAll();
      return CachedValueProvider.Result.create(methodList, PsiModificationTracker.MODIFICATION_COUNT);
    });
    psiMethods.forEach(m -> {
      PsiClass opClass = m.getContainingClass();
      if (opClass == null)
        return;
      String className = opClass.getName();
      if(className == null || !className.endsWith("Operation") || !className.startsWith(capitalizedMethod))
        return;
      var field = opClass.findFieldByName("OPERATION_PATH", true);
      if (field == null)
        return;
      PsiLiteralValue initializer = (PsiLiteralValue) field.getInitializer();
      if(initializer == null || !Objects.equals(initializer.getValue(), path))
        return;
      classes.add(opClass);
    });
  }

  @NotNull
  private String toValidPackageName(PsiElement psiElement) {
    return JavaUtils.removeJavaPackageUnwantedCharacters(psiElement.getText()).toLowerCase();
  }
}
