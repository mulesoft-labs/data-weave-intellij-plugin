package org.mule.tooling.gcl.schema;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.SchemaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.SelectionPath;

public abstract class AbstractGCLFileProvider implements JsonSchemaFileProvider {
    static SelectionPath KIND_SELECTION = SelectionPath.DOCUMENT.child("kind");
    static SelectionPath API_VERSION_SELECTION = SelectionPath.DOCUMENT.child("apiVersion");
    protected final Project project;

    public AbstractGCLFileProvider(@NotNull Project project) {
        this.project = project;
    }

    @Nullable
    public static String getKind(Project project, VirtualFile file) {
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile != null) {
            if (psiFile.getFileType() == YAMLFileType.YML) {
                final YAMLDocument[] childrenOfType = PsiTreeUtil.getChildrenOfType(psiFile, YAMLDocument.class);
                if (childrenOfType == null) {
                    return null;
                }
                for (YAMLDocument yamlDocument : childrenOfType) {
                    final PsiElement psiElement = KIND_SELECTION.selectYaml(yamlDocument);
                    if (psiElement instanceof YAMLScalar) {
                        return ((YAMLScalar) psiElement).getTextValue();
                    } else {
                        return psiElement != null ? "" : null;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getApiInstance(Project project, VirtualFile file) {
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile != null) {
            if (psiFile.getFileType() == YAMLFileType.YML) {
                final YAMLDocument[] childrenOfType = PsiTreeUtil.getChildrenOfType(psiFile, YAMLDocument.class);
                if (childrenOfType == null) {
                    return null;
                }
                for (YAMLDocument yamlDocument : childrenOfType) {
                    final PsiElement psiElement = API_VERSION_SELECTION.selectYaml(yamlDocument);
                    if (psiElement instanceof YAMLScalar) {
                        return ((YAMLScalar) psiElement).getTextValue();
                    } else {
                        return psiElement != null ? "" : null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull SchemaType getSchemaType() {
        return SchemaType.userSchema;
    }
}
