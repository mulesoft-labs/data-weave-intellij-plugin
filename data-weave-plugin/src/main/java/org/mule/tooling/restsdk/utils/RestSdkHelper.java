package org.mule.tooling.restsdk.utils;

import amf.client.model.domain.WebApi;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLScalar;
import webapi.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestSdkHelper {

  public static YamlPath swaggerVersion = YamlPath.DOCUMENT.child("swagger");
  public static YamlPath openApiVersion = YamlPath.DOCUMENT.child("openapi");


  public static boolean isInRestSdkContextFile(PsiFile psiFile) {
    PsiElement context = psiFile.getContext();
    if (context instanceof YAMLScalar) {
      PsiFile containingFile = context.getContainingFile();
      return isRestSdkDescriptorFile(containingFile);
    } else {
      return false;
    }
  }

  public static boolean isRestSdkDescriptorFile(PsiFile containingFile) {
    String text = containingFile.getText();
    return isRestSdkDescriptor(text);
  }

  public static boolean isRestSdkDescriptor(String text) {
    return text.contains("#% Rest Connector Descriptor 1.0");
  }


  public static WebApi parseWebApi(PsiFile restSdkFile) {
    WebApi result = null;
    final PsiElement select = RestSdkPaths.API_PATH.select(restSdkFile);
    if (select instanceof YAMLScalar) {
      String apiPath = ((YAMLScalar) select).getTextValue();
      //
      final VirtualFile parent = restSdkFile.getVirtualFile().getParent();
      final VirtualFile child = parent.findFileByRelativePath(apiPath);
      if (child != null) {
        result = parseWebApi(restSdkFile.getProject(), child);
      }
    }
    return result;
  }

  public static @Nullable WebApi parseWebApi(Project project, VirtualFile child) {
    WebApi result = null;
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(child);
    if (psiFile != null) {
      CompletableFuture<WebApiBaseUnit> parse;
      if (psiFile.getFileType() instanceof YAMLFileType) {
        if (swaggerVersion.select(psiFile) != null) {
          parse = Oas20.parseYaml(child.getUrl());
        } else if (openApiVersion.select(psiFile) != null) {
          parse = Oas30.parseYaml(child.getUrl());
        } else {
          parse = Raml10.parse(child.getUrl());
        }
      } else if (psiFile.getFileType() instanceof JsonFileType) {
        //TODO try to detect version better
        parse = Oas30.parse(child.getUrl());
      } else {
        return null;
      }
      try {
        final WebApiDocument webApiBaseUnit = (WebApiDocument) parse.get();
        result = (WebApi) webApiBaseUnit.encodes();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}
