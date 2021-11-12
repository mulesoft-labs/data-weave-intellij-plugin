package org.mule.tooling.restsdk.reference;

import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.common.position.Position;
import amf.core.client.platform.model.document.Document;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

public class OperationIDPathReference extends PsiReferenceBase<YAMLScalar> {

  public OperationIDPathReference(@NotNull final YAMLScalar element) {
    super(element);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    if (RestSdkHelper.isRestSdkDescriptorFile(myElement.getContainingFile())) {
      final String operationID = myElement.getTextValue();
      final Document webApiDocument = RestSdkHelper.parseWebApi(myElement.getOriginalElement().getContainingFile());
      final PsiFile apiFile = RestSdkHelper.apiFile(myElement.getContainingFile());
      if (webApiDocument != null && apiFile != null) {
        final WebApi webApi = (WebApi) webApiDocument.encodes();
        final Operation operation = RestSdkHelper.operationById(webApi, operationID);
        if (operation != null) {
          final Position start = operation.position().start();
          final int offset = StringUtil.lineColToOffset(apiFile.getText(), start.line(), start.column());
          return PsiUtil.getElementAtOffset(apiFile, offset);
        }
      }
    }
    return null;
  }

}
