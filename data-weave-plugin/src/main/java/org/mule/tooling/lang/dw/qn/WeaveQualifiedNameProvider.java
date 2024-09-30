package org.mule.tooling.lang.dw.qn;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

public class WeaveQualifiedNameProvider implements QualifiedNameProvider {
    @Nullable
    @Override
    public PsiElement adjustElementToCopy(PsiElement psiElement) {
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName(PsiElement psiElement) {
        if (psiElement instanceof PsiQualifiedNamedElement) {
            return ((PsiQualifiedNamedElement) psiElement).getQualifiedName();
        } else if (psiElement instanceof PsiFile) {
            boolean isDataFile = ((PsiFile) psiElement).getFileType() == WeaveFileType.getInstance();
            if (isDataFile) {
                final NameIdentifier nameIdentifier = WeavePsiImplUtils.getNameIdentifier((PsiFile) psiElement);
                return nameIdentifier.toString();
            }
        } else if (psiElement instanceof WeaveFunctionDefinition || psiElement instanceof WeaveVariableDefinition || psiElement instanceof WeaveAnnotationDefinition) {
            //WeaveFunctionDirective -> HeaderNode -> DocumentNode
            if (getParent(psiElement, 3) instanceof WeaveDocument) {
                final WeaveDocument document = WeavePsiUtils.getDocument(psiElement);
                if (document != null) {
                    return document.getQualifiedName() + NameIdentifier.SEPARATOR() + ((WeaveNamedElement) psiElement).getName();
                }
            }
        } else if (psiElement instanceof WeaveTypeDirective) {
            // HeaderNode -> DocumentNode
            if (getParent(psiElement, 2) instanceof WeaveDocument) {
                final WeaveDocument document = WeavePsiUtils.getDocument(psiElement);
                if (document != null) {
                    return document.getQualifiedName() + NameIdentifier.SEPARATOR() + ((WeaveNamedElement) psiElement).getName();
                }
            }
        }
        return null;
    }


    @Nullable
    private PsiElement getParent(PsiElement psiElement, int level) {
        int i = 0;
        PsiElement parent = psiElement;
        while (level != i && parent != null) {
            parent = parent.getParent();
            i++;
        }
        if (level == i) {
            return parent;
        } else {
            return null;
        }

    }

    @Override
    public PsiElement qualifiedNameToElement(String fqn, Project project) {
        NameIdentifier nameIdentifier = NameIdentifier.apply(fqn, Option.empty());
        PsiElement psiElement = getPsiElement(project, nameIdentifier);
        if (psiElement == null) {
            //If it is not a module reference try an element reference
            Option<NameIdentifier> mayBeParent = nameIdentifier.parent();
            if (mayBeParent.isDefined()) {
                NameIdentifier parentElement = mayBeParent.get();
                String searchElement = nameIdentifier.name();
                PsiElement module = getPsiElement(project, parentElement);
                if (module != null) {
                    WeaveDocument document = WeavePsiUtils.getDocument(module);
                    if (document != null) {
                        return WeavePsiUtils.resolveReference(document, searchElement);
                    }
                }
            }
        }
        return psiElement;
    }

    @Nullable
    public PsiElement getPsiElement(Project project, NameIdentifier nameIdentifier) {
        String fileRelativePath = NameIdentifierHelper.toWeaveFilePath(nameIdentifier);
        List<PsiElement> psiElements = getPsiElements(project, fileRelativePath);
        if (psiElements.isEmpty()) {
            return null;
        } else {
            return psiElements.get(0);
        }
    }

    public List<PsiElement> getPsiElements(Project project, String fileRelativePath) {
        final String relativePath = fileRelativePath.startsWith("/") ? fileRelativePath : "/" + fileRelativePath;
        final FileType stdFileType = FileTypeManager.getInstance().getFileTypeByFileName(FilenameUtils.getName(relativePath));
        final List<VirtualFile> fileList = new ArrayList<>();
        FileTypeIndex.processFiles(stdFileType, virtualFile -> {
            if (virtualFile.getPath().endsWith(relativePath)) {
                fileList.add(virtualFile);
            }
            return true;
        }, GlobalSearchScope.allScope(project));
        final ArrayList<PsiElement> result = new ArrayList<>();
        for (VirtualFile virtualFile : fileList) {
            result.add(PsiManager.getInstance(project).findFile(virtualFile));
        }
        return result;
    }


    @Override
    public void insertQualifiedName(String fqn, PsiElement psiElement, Editor editor, Project project) {
        EditorModificationUtil.insertStringAtCaret(editor, fqn);
    }
}
