package org.mule.tooling.restsdk.annotator;

import amf.apicontract.client.platform.model.domain.api.WebApi;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.List;

public class RestSdkAnnotator implements Annotator {
  final private static List<String> URL_KEY_NAMES = List.of("url", "typeSchema", "outputType");

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    PsiFile psiFile = element.getContainingFile();
    if (!RestSdkHelper.isRestSdkDescriptorFile(psiFile))
      return;
    PsiElement parent = element.getParent();
    if (parent instanceof YAMLKeyValue) {
      if (element instanceof YAMLScalar) {
        if (URL_KEY_NAMES.contains(((YAMLKeyValue) parent).getKeyText())) {
          VirtualFile virtualFile = psiFile.getVirtualFile();
          if (virtualFile != null) {
            String path = ((YAMLScalar) element).getTextValue();
            VirtualFile virtualFileParent = virtualFile.getParent();
            if (virtualFileParent != null) {
              final VirtualFile child = virtualFileParent.findFileByRelativePath(path);
              if (child == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unable to resolve path to : " + path).range(element).create();
              }
            }
          }
        }
      } else if (element == ((YAMLKeyValue) parent).getKey()) {
        var mapping = (YAMLMapping) parent.getParent();
        SelectionPath yamlPath = SelectionPath.pathOfYaml(mapping);
        if(yamlPath.matches(RestSdkPaths.ENDPOINTS_METHOD_PATH)) {
          var endpointPath = ((YAMLKeyValue)mapping.getParent().getParent().getParent()).getKeyText();
          String httpMethod = ((YAMLKeyValue) parent).getKeyText();
          var webApiDocument = RestSdkHelper.parseWebApi(psiFile);
          if (webApiDocument == null)
            return;
          WebApi webApi = (WebApi) webApiDocument.encodes();
          var endpoint = RestSdkHelper.endpointByPath(webApi, endpointPath);
          if (endpoint != null && RestSdkHelper.getEndpointMethods(endpoint).noneMatch(m -> m.equals(httpMethod)))
            holder.newAnnotation(HighlightSeverity.ERROR, "There´s no " + httpMethod + " method in endpoint " + endpointPath)
                    .range(element)
                    .create();
        }
      }
    }
  }
}
