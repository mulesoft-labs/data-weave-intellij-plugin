package org.mule.tooling.lang.dw.indexer;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.editor.indexing.*;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IJDataWeaveIndexerService implements WeaveIndexService {
    private Project project;

    public IJDataWeaveIndexerService(Project project) {
        this.project = project;
    }

    @Override
    public LocatedResult[] searchDefinitions(String name) {
        final List<List<WeaveGlobalDefinitionsIndexer.GlobalElementIdentifier>> values =
                FileBasedIndex.getInstance()
                        .getValues(WeaveGlobalDefinitionsIndexer.INDEX_ID, name, GlobalSearchScope.allScope(project));
        return values.stream().flatMap((l) -> {
            return l.stream().map((gi) -> {
                final WeaveIdentifier weaveIdentifier = new WeaveIdentifier(gi.startLocation(), gi.endLocation(), gi.value(), gi.idType(), gi.kind());
                final NameIdentifier nameIdentifier = NameIdentifier.apply(gi.moduleName(), Option.empty());
                return new LocatedResult<>(nameIdentifier, weaveIdentifier);
            });
        }).toArray(LocatedResult[]::new);
    }

    @Override
    public LocatedResult<WeaveIdentifier>[] searchReferences(String name) {
        return new LocatedResult[0];
    }

    @Override
    public Iterator<LocatedResult<WeaveDocument>> searchDocumentContainingName(String name) {
        final List<LocatedResult<WeaveDocument>> fileList = new ArrayList<>();
        FileTypeIndex.processFiles(WeaveFileType.getInstance(), virtualFile -> {
            NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, virtualFile);
            if (nameIdentifier.toString().contains(name)) {
                PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                if (file != null) {
                    org.mule.tooling.lang.dw.parser.psi.WeaveDocument weaveDocument = PsiTreeUtil.getChildOfType(file, org.mule.tooling.lang.dw.parser.psi.WeaveDocument.class);
                    if (weaveDocument != null) {
                        int documentKind = weaveDocument.isMappingDocument() ? DocumentKind.MAPPING() : DocumentKind.MODULE();
                        fileList.add(new LocatedResult<WeaveDocument>(nameIdentifier, new WeaveDocument("", documentKind)));
                    }
                }
            }
            return true;
        }, GlobalSearchScope.allScope(project));
        return fileList.iterator();
    }
}
