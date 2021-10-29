package org.mule.tooling.restsdk.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.mule.tooling.restsdk.templates.RamlFilesTemplateManager;
import org.mule.tooling.restsdk.utils.RestSdkIcons;

public class CreateNewApiFileAction extends CreateFileFromTemplateAction implements DumbAware {

  public CreateNewApiFileAction() {
    super("Api File", "Create new API file.", RestSdkIcons.RamlFileType);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
    builder.setTitle("API File")
            .addKind("OAS File", RestSdkIcons.OASFileType, RamlFilesTemplateManager.OAS3)
            .addKind("RAML API specification", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.RAML_FILE)
            .addKind("RAML Annotation type declaration", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.ANNOTATION_TYPE_DECLARATION)
            .addKind("RAML Data type", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.DATA_TYPE)
            .addKind("RAML Documentation item", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.DOCUMENTATION_ITEM)
            .addKind("RAML Extension", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.EXTENSION)
            .addKind("RAML Library", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.LIBRARY)
            .addKind("RAML Named example", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.NAMED_EXAMPLE)
            .addKind("RAML Overlay", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.OVERLAY)
            .addKind("RAML Resource type", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.RESOURCE_TYPE)
            .addKind("RAML Security scheme", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.SECURITY_SCHEME)
            .addKind("RAML Trait", RestSdkIcons.RamlFileType, RamlFilesTemplateManager.TRAIT)
            ;
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
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

