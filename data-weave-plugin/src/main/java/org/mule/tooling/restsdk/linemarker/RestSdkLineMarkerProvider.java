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
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.mule.tooling.restsdk.utils.JavaUtils;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.openapi.util.text.Strings.capitalize;

public class RestSdkLineMarkerProvider extends RelatedItemLineMarkerProvider {

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    Project myProject = element.getProject();
    if (element instanceof YAMLKeyValue) {
      SelectionPath selectionPath = SelectionPath.pathOfYaml(element);
      SelectionPath parent = selectionPath.getParent();
      if (parent != null && parent.getName().equals(RestSdkPaths.OPERATIONS)) {
        // Add the property to a collection of line marker info

        PsiFile containingFile = element.getContainingFile();

        PsiElement psiElement = RestSdkPaths.CONNECTOR_NAME_PATH.selectYaml(containingFile);
        if (psiElement != null && psiElement.getText() != null) {
          String packageName = "com.mulesoft.connectors." + JavaUtils.removeJavaPackageUnwantedCharacters(psiElement.getText()).toLowerCase() + ".internal.operation";
          String name = selectionPath.getName();
          String operationClassName = capitalize(name) + "Operation";
          List<PsiClass> results = new ArrayList<>();
          JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(myProject);
          PsiClass operationClass = javaPsiFacade.findClass(packageName + "." + operationClassName, ProjectScope.getProjectScope(myProject));
          if (operationClass != null) {
            results.add(operationClass);
          }

          PsiClass operationClassIntereceptor = javaPsiFacade.findClass(packageName + "." + "interceptor" + "." + operationClassName + "Interceptor", ProjectScope.getProjectScope(myProject));
          if (operationClassIntereceptor != null) {
            results.add(operationClassIntereceptor);
          }

          PsiClass operationClassBase = javaPsiFacade.findClass(packageName + "." + "base" + "." + operationClassName + "Base", ProjectScope.getProjectScope(myProject));
          if (operationClassBase != null) {
            results.add(operationClassBase);
          }

          if (!results.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod)
                            .setTargets(results)
                            .setTooltipText("Click to navigate to generated class.");
            result.add(builder.createLineMarkerInfo(element));
          }

        }


      }
    }

  }
}
