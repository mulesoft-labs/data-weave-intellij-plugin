package org.mule.tooling.lang.dw.parser.psi;


import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.reference.WeaveModuleReferenceSet;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class WeavePsiImplUtils {

    public static ItemPresentation getPresentation(WeaveDocument document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "Document";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return WeaveIcons.DataFileType;
            }
        };
    }

    @Nullable
    public static String getName(WeaveDocument document) {
        final NameIdentifier nameIdentifier = getNameIdentifier(document);
        if (nameIdentifier != null) {
            return nameIdentifier.name();
        } else {
            return null;
        }
    }

    @Nullable
    private static NameIdentifier getNameIdentifier(WeaveDocument document) {
        final PsiFile containingFile = document.getContainingFile();
        return getNameIdentifier(containingFile);
    }

    @Nullable
    public static NameIdentifier getNameIdentifier(PsiFile containingFile) {
        final Project project = containingFile.getProject();
        final VirtualFile vfs = containingFile.getVirtualFile();
        final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(vfs);
        if (contentRootForFile != null) {
            final String relPath = VfsUtil.getRelativePath(vfs, contentRootForFile);
            return NameIdentifierHelper.fromWeaveFilePath(relPath);
        } else {
            return null;
        }
    }

    @Nullable
    public static String getQualifiedName(WeaveDocument document) {
        final NameIdentifier nameIdentifier = getNameIdentifier(document);
        if (nameIdentifier != null) {
            return nameIdentifier.toString();
        } else {
            return null;
        }
    }

    public static WeaveDocument setName(WeaveDocument document, String name) {
        document.getContainingFile().setName(name);
        return document;
    }

    public static ItemPresentation getPresentation(WeaveObjectExpression document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Object;
            }
        };
    }

    public static ItemPresentation getPresentation(WeaveArrayExpression document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Array;
            }
        };
    }

    public static ItemPresentation getPresentation(final WeaveSimpleKeyValuePair kvp) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return kvp.getKey().getExpression().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Property_braces;
            }
        };
    }

    @Nullable
    public static WeaveExpression getLeft(WeaveBinaryExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (!expressionList.isEmpty()) {
            return expressionList.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    public static WeaveExpression getRight(WeaveBinaryExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (expressionList.size() > 0) {
            return expressionList.get(1);
        } else {
            return null;
        }
    }


    public static String getPath(WeaveModuleReference moduleReference) {
        WeaveIdentifierPackage identifierPackage = moduleReference.getIdentifierPackage();
        return identifierPackage.getIdentifierList().stream().map(WeaveIdentifier::getName).collect(Collectors.joining("/")) + "/" + moduleReference.getIdentifier().getName() + "." + WeaveFileType.WeaveFileExtension;
    }

    public static String getModuleFQN(WeaveModuleReference moduleReference) {
        WeaveIdentifierPackage identifierPackage = moduleReference.getIdentifierPackage();
        if (identifierPackage.getText().trim().isEmpty()) {
            return moduleReference.getIdentifier().getName();
        } else {
            return identifierPackage.getText() + moduleReference.getIdentifier().getName();
        }
    }

    @NotNull
    public static PsiReference[] getReferences(WeaveModuleReference importDirective) {
        return new WeaveModuleReferenceSet(importDirective).getAllReferences();
    }


    @NotNull
    public static String getValue(WeaveStringLiteral stringLiteral) {
        return WeavePsiUtils.stripQuotes(stringLiteral.getText());
    }

    public static String getName(WeaveIdentifier identifier) {
        return identifier.getText();
    }

    //Variable Reference
    public static String getVariableName(WeaveVariableReferenceExpression variable) {
        return variable.getText();
    }

    public static WeaveIdentifier getIdentifier(WeaveVariableReferenceExpression variable) {
        return variable.getFqnIdentifier().getIdentifier();
    }

//  public static PsiReference getReference(WeaveVariableReferenceExpression variable) {
//    return new WeaveIdentifierPsiReference(variable, new TextRange(0, variable.getText().length()));
//  }

    //Function Call
//    public static PsiReference getReference(WeaveFunctionCallExpression variable) {
//        return new WeaveFunctionPsiReference(variable, new TextRange(0, variable.getName().length()));
//    }

}
