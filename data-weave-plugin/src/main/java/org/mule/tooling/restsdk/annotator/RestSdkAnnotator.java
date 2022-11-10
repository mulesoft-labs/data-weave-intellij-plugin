package org.mule.tooling.restsdk.annotator;

import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import static com.intellij.util.ObjectUtils.*;

public class RestSdkAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    PsiFile psiFile = element.getContainingFile();
    if (!RestSdkHelper.isRestSdkDescriptorFile(psiFile))
      return;
    PsiElement parent = element.getParent();
    if (parent instanceof YAMLKeyValue) {
      String key = ((YAMLKeyValue) parent).getKeyText();
      if (element instanceof YAMLScalar) {
        switch (key) {
          case "url":
          case "typeSchema":
          case "outputType":
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
            break;
          case "path":
            checkEndpointPath(holder, psiFile, ((YAMLScalar) element).getTextValue());
            break;
          case "method":
            var endpointPath = doIfNotNull(YAMLUtil.findKeyInProbablyMapping(((YAMLKeyValue)parent).getParentMapping(), "path"), YAMLKeyValue::getValueText);
            if (endpointPath != null)
              checkHttpMethod(holder, psiFile, endpointPath, ((YAMLScalar) element).getTextValue());
            break;
        }
      } else if (element == ((YAMLKeyValue) parent).getKey()) {
        var mapping = (YAMLMapping) parent.getParent();
        SelectionPath yamlPath = SelectionPath.pathOfYaml(mapping);
        if (yamlPath.matches(RestSdkPaths.ENDPOINTS_METHOD_PATH))
          checkHttpMethod(holder, psiFile, ((YAMLKeyValue) mapping.getParent().getParent().getParent()).getKeyText(), key);
        else if (yamlPath.matches(RestSdkPaths.ENDPOINTS_PATH))
          checkEndpointPath(holder, psiFile, key);
        else if (doIfCast(mapping.getParent(), YAMLKeyValue.class, kv -> kv.getKeyText().equals(RestSdkPaths.URI_PARAMETERS)) == Boolean.TRUE) {
          checkUriParameter(holder, element);
        }
      }
    }
  }

  private static void checkUriParameter(@NotNull AnnotationHolder holder, @NotNull PsiElement element) {
    String path = doIfCast(RestSdkPaths.RELATIVE_TRIGGER_PATH_FROM_BINDING_URIPARAMETER_PATH.selectYaml(element), YAMLScalar.class, YAMLScalar::getTextValue);
    if (path == null)
      return;
    String parameterName = ((YAMLKeyValue) element.getParent()).getKeyText();
    if(!path.contains("{" + parameterName + "}"))
      holder.newAnnotation(HighlightSeverity.ERROR,"URI parameter '" + parameterName + "' not in path '" + path + "'")
              .create();
  }

  private static void checkHttpMethod(@NotNull AnnotationHolder holder, @NotNull PsiFile psiFile, @NotNull String endpointPath, @NotNull String httpMethod) {
    EndPoint endpoint = getEndPoint(psiFile, endpointPath);
    if (endpoint != null && RestSdkHelper.getEndpointMethods(endpoint).noneMatch(m -> m.equals(httpMethod)))
      holder.newAnnotation(HighlightSeverity.ERROR, "There´s no " + httpMethod + " method in endpoint " + endpointPath)
              .create();
  }

  private static void checkEndpointPath(@NotNull AnnotationHolder holder, @NotNull PsiFile psiFile, @NotNull String endpointPath) {
    EndPoint endpoint = getEndPoint(psiFile, endpointPath);
    if (endpoint == null)
      holder.newAnnotation(HighlightSeverity.ERROR, "Endpoint not present in API spec: " + endpointPath)
              .create();
  }

  @Nullable
  private static EndPoint getEndPoint(PsiFile psiFile, String endpointPath) {
    return doIfNotNull(RestSdkHelper.parseWebApi(psiFile),
            webApiDocument -> RestSdkHelper.endpointByPath((WebApi) webApiDocument.encodes(), endpointPath));
  }
}
