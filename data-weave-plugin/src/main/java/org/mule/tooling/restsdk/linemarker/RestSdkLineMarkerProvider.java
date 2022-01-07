package org.mule.tooling.restsdk.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.mule.tooling.restsdk.utils.JavaUtils;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    Project myProject = element.getProject();
    if (element instanceof YAMLKeyValue) {
      final SelectionPath selectionPath = SelectionPath.pathOfYaml(element);
      final SelectionPath parent = selectionPath.getParent();
      if (parent != null) {
        if (parent.getName().equals(RestSdkPaths.OPERATIONS) || parent.getName().equals(RestSdkPaths.TRIGGERS)) {
          // Add the property to a collection of line marker info
          final PsiFile containingFile = element.getContainingFile();
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

            List<PsiClass> results = new ArrayList<>();
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

            if (!results.isEmpty()) {
              final NavigationGutterIconBuilder<PsiElement> builder =
                      NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                              .setTargets(results)
                              .setTooltipText("Click to navigate to Generated Classes.");
              result.add(builder.createLineMarkerInfo(element));
            }
          }
        } else if (parent.getName().equals(RestSdkPaths.SAMPLE_DATA)) {

          final PsiFile containingFile = element.getContainingFile();
          final PsiElement psiElement = RestSdkPaths.CONNECTOR_NAME_PATH.selectYaml(containingFile);
          if (psiElement != null && psiElement.getText() != null) {
            final String name = selectionPath.getName();

            String packageName;
            String sampleDataClassName;

            packageName = DEFAULT_BASE_PACKAGE + toValidPackageName(psiElement) + "." + INTERNAL_PACKAGE_NAME + "." + "metadata" + "." + "sample";
            sampleDataClassName = capitalize(name) + "SampleDataProvider";
            List<PsiClass> results = new ArrayList<>();
            final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(myProject);
            final PsiClass operationClass = javaPsiFacade.findClass(packageName + "." + sampleDataClassName, ProjectScope.getProjectScope(myProject));
            if (operationClass != null) {
              results.add(operationClass);
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
      }
    }
  }

  @NotNull
  private String toValidPackageName(PsiElement psiElement) {
    return JavaUtils.removeJavaPackageUnwantedCharacters(psiElement.getText()).toLowerCase();
  }
}
