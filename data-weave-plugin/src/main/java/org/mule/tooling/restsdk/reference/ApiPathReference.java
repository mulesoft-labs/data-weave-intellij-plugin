package org.mule.tooling.restsdk.reference;

import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.common.position.Position;
import amf.core.client.platform.model.document.Document;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.util.List;
import java.util.stream.Collectors;

public class ApiPathReference extends PsiReferenceBase<PsiElement> {

  private final String apiPath;
  private final boolean isKey;

  /** Creates a reference to an endpoint path.
   *
   * @param element   the underlying element
   * @param apiPath   the URL (taken from the element)
   * @param isKey     whether the element represents a YAML key and so a colon should follow
   */
  public ApiPathReference(@NotNull final PsiElement element, String apiPath, boolean isKey) {
    super(element);
    this.apiPath = apiPath;
    this.isKey = isKey;
  }

  @Override
  public Object @NotNull [] getVariants() {
    PsiFile psiFile = myElement.getContainingFile();
    if (!RestSdkHelper.isRestSdkDescriptorFile(psiFile))
      return super.getVariants();
    final Document webApiDocument = RestSdkHelper.parseWebApi(myElement.getOriginalElement().getContainingFile());
    final PsiFile apiFile = RestSdkHelper.apiFile(psiFile);
    if (webApiDocument == null || apiFile == null)
      return super.getVariants();
    final WebApi webApi = (WebApi) webApiDocument.encodes();
    List<EndPoint> endPoints = webApi.endPoints();
    LookupElementBuilder[] result = new LookupElementBuilder[endPoints.size()];
    for (int i = 0; i < endPoints.size(); i++) {
      EndPoint endPoint = endPoints.get(i);
      LookupElementBuilder lookupElement = LookupElementBuilder.create(endPoint.path().value())
              .withTypeText(RestSdkHelper.getEndpointMethods(endPoint).collect(Collectors.joining(", ")), true)
              .withIcon(AllIcons.Nodes.Property);
      if (isKey) {
        lookupElement = lookupElement
                .withTailText(":", true)
                .withInsertHandler((context, item) -> {
                  String text = context.getDocument().getText();
                  int tailOffset = context.getTailOffset();
                  if (!needsColon(text, tailOffset))
                    return;
                  context.getDocument().insertString(tailOffset, ":");
                  context.getEditor().getCaretModel().moveToOffset(tailOffset + 1);
                });
      }
      result[i] = lookupElement;
    }
    return result;
  }

  private static boolean needsColon(@NotNull String text, int offset) {
    for (int pos = offset; pos < text.length(); pos++) {
      switch (text.charAt(pos)) {
        case ':':
          return false;
        case '\n':
        case '\r':
          return true;
      }
    }
    return true;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    PsiFile psiFile = myElement.getContainingFile();
    if (RestSdkHelper.isRestSdkDescriptorFile(psiFile)) {
      final Document webApiDocument = RestSdkHelper.parseWebApi(myElement.getOriginalElement().getContainingFile());
      final PsiFile apiFile = RestSdkHelper.apiFile(psiFile);
      if (webApiDocument != null && apiFile != null) {
        final WebApi webApi = (WebApi) webApiDocument.encodes();
        final PsiElement method = SelectionPath.PARENT.child("method").selectYaml(myElement);
        if (method instanceof YAMLScalar) {
          final String methodName = ((YAMLScalar) method).getTextValue();
          final Operation operation = RestSdkHelper.operationByMethodPath(webApi, methodName, apiPath);
          if (operation != null) {
            final Position start = operation.position().start();
            final int offset = StringUtil.lineColToOffset(apiFile.getText(), start.line(), start.column());
            return PsiUtil.getElementAtOffset(apiFile, offset);
          }
        } else {
          final EndPoint endpoint = RestSdkHelper.endpointByPath(webApi, apiPath);
          if (endpoint != null) {
            final Position start = endpoint.position().start();
            int offset = StringUtil.lineColToOffset(apiFile.getText(), start.line(), start.column());
            return PsiUtil.getElementAtOffset(apiFile, offset);
          }
        }
      }
    }
    return null;
  }
}
