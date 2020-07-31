package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.reference.WeaveIdentifierPsiReference;
import org.mule.tooling.lang.dw.reference.WeaveModuleReferenceSet;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WeavePsiImplUtils {

    public static ItemPresentation getPresentation(WeaveDocument document) {
        return new ItemPresentation() {

            @Override
            public String getPresentableText() {
                return document.getName();
            }

            @Override
            public String getLocationString() {
                return document.getQualifiedName();
            }

            @Override
            public Icon getIcon(boolean b) {
                return WeaveIcons.DataWeaveModuleIcon;
            }
        };
    }

    @NotNull
    public static String getName(WeaveDocument document) {
        final NameIdentifier nameIdentifier = getNameIdentifier(document);
        final NameIdentifier localName = nameIdentifier == null ? null : nameIdentifier.localName();
        return localName == null ? "" : localName.name();
    }

    @Nullable
    private static NameIdentifier getNameIdentifier(WeaveDocument document) {
        final PsiFile containingFile = document.getContainingFile();
        if (containingFile == null) {
            return null;
        }
        return getNameIdentifier(containingFile);
    }

    public static NameIdentifier getNameIdentifier(PsiFile containingFile) {
        return VirtualFileSystemUtils.calculateNameIdentifier(containingFile.getProject(), containingFile.getVirtualFile());
    }

    @NotNull
    public static String getQualifiedName(WeaveDocument document) {
        final NameIdentifier nameIdentifier = getNameIdentifier(document);
        return nameIdentifier == null ? "" : nameIdentifier.name();
    }

    public static WeaveDocument setName(WeaveDocument document, String name) {
        document.getContainingFile().setName(name);
        return document;
    }

    public static ItemPresentation getPresentation(WeaveObjectExpression document) {
        return new ItemPresentation() {

            @NotNull
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

            @NotNull
            @Override
            public String getPresentableText() {
                return "";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Array;
            }
        };
    }

    public static ItemPresentation getPresentation(final WeaveKeyValuePair kvp) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return kvp.getKey().getText();
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

    public static boolean isMappingDocument(WeaveDocument document) {
        return document.getBody() != null;
    }

    public static boolean isModuleDocument(WeaveDocument document) {
        return document.getBody() == null;
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


    public static String getPath(WeaveFqnIdentifier moduleReference) {
        WeaveContainerModuleIdentifier identifierPackage = moduleReference.getContainerModuleIdentifier();
        return identifierPackage.getIdentifierList().stream().map(WeaveIdentifier::getName).collect(Collectors.joining("/")) + "/" + moduleReference.getIdentifier().getName() + "." + WeaveFileType.WeaveFileExtension;
    }

    public static String getModuleFQN(WeaveFqnIdentifier moduleReference) {
        WeaveContainerModuleIdentifier identifierPackage = moduleReference.getContainerModuleIdentifier();
        if (identifierPackage.getText().trim().isEmpty()) {
            return moduleReference.getIdentifier().getName();
        } else {
            return identifierPackage.getText() + moduleReference.getIdentifier().getName();
        }
    }

    public static PsiReference[] getReferences(WeaveBinaryFunctionIdentifier identifier) {
        List<PsiReference> result = new ArrayList<>();
        if (!identifier.getContainerModuleIdentifier().getIdentifierList().isEmpty()) {
            String containerModuleFQN = getContainerModuleFQN(identifier);
            PsiReference[] allReferences = new WeaveModuleReferenceSet(identifier, containerModuleFQN).getAllReferences();
            result.addAll(Arrays.asList(allReferences));
        }
        result.add(new WeaveIdentifierPsiReference(identifier));
        return result.toArray(new PsiReference[result.size()]);
    }

    public static String getContainerModuleFQN(WeaveBinaryFunctionIdentifier identifier) {
        WeaveContainerModuleIdentifier identifierPackage = identifier.getContainerModuleIdentifier();
        return identifierPackage.getIdentifierList().stream().map(i -> i.getName()).reduce((v, a) -> v + "::" + a).orElse("");
    }

    public static PsiReference[] getReferences(WeaveFqnIdentifier identifier) {
        List<PsiReference> result = new ArrayList<>();
        if (!identifier.getContainerModuleIdentifier().getIdentifierList().isEmpty()) {
            String containerModuleFQN = getContainerModuleFQN(identifier);
            PsiReference[] allReferences = new WeaveModuleReferenceSet(identifier, containerModuleFQN).getAllReferences();
            result.addAll(Arrays.asList(allReferences));
        }
        result.add(new WeaveIdentifierPsiReference(identifier));
        return result.toArray(new PsiReference[result.size()]);
    }


    public static String getContainerModuleFQN(WeaveFqnIdentifier identifier) {
        WeaveContainerModuleIdentifier identifierPackage = identifier.getContainerModuleIdentifier();
        return identifierPackage.getIdentifierList().stream().map(i -> i.getName()).reduce((v, a) -> v + "::" + a).orElse("");
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
//        return new WeaveFunctionPsiReference(variable, new TextRange(0, variable.getParamName().length()));
//    }

}
