package org.mule.tooling.als.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.als.templates.RamlFilesTemplateManager;
import org.mule.tooling.als.utils.RestSdkIcons;
import org.mule.tooling.gcl.GCLFileTemplateManager;


public class CreateNewApiFileAction extends CreateFileFromTemplateAction implements DumbAware {

  public CreateNewApiFileAction() {
    super("Api File", "Create new API file.", RestSdkIcons.RamlFileType);
  }

  @Override
  protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
    builder.setTitle("API File")
            .addKind("OAS file", RestSdkIcons.OASFileType, RamlFilesTemplateManager.OAS3)
            .addKind("GCL", RestSdkIcons.OASFileType, GCLFileTemplateManager.GCL)
            .addKind("RAML API specification", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.RAML_FILE)
            .addKind("RAML annotation type declaration", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.ANNOTATION_TYPE_DECLARATION)
            .addKind("RAML data type", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.DATA_TYPE)
            .addKind("RAML documentation item", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.DOCUMENTATION_ITEM)
            .addKind("RAML extension", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.EXTENSION)
            .addKind("RAML library", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.LIBRARY)
            .addKind("RAML named example", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.NAMED_EXAMPLE)
            .addKind("RAML overlay", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.OVERLAY)
            .addKind("RAML resource type", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.RESOURCE_TYPE)
            .addKind("RAML security scheme", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.SECURITY_SCHEME)
            .addKind("RAML trait", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.TRAIT)
            ;
  }

  @Override
  protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
    return "Create " + newName;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CreateNewApiFileAction;
  }
}

