package org.mule.tooling.lang.dw.service;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;
import org.mule.weave.lsp.DWTextDocumentService;
import org.mule.weave.v2.completion.EmptyDataFormatDescriptorProvider$;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DataWeaveServiceManager extends AbstractProjectComponent {

    private IntellijVirtualFileSystemAdaptor projectVirtualFileSystem;
    private DWTextDocumentService dwTextDocumentService;

    protected DataWeaveServiceManager(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        projectVirtualFileSystem = new IntellijVirtualFileSystemAdaptor(myProject);
        dwTextDocumentService = new DWTextDocumentService(projectVirtualFileSystem, EmptyDataFormatDescriptorProvider$.MODULE$, myProject.getBasePath());
    }

    public List<LookupElement> completion(CompletionParameters completionParameters) {
        try {
            String uri = completionParameters.getOriginalFile().getVirtualFile().getUrl();
            final TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
            final Editor editor = completionParameters.getEditor();
            dwTextDocumentService.didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(uri, "DW", 1, completionParameters.getEditor().getDocument().getText())));
            final int offset = completionParameters.getOffset();
            final Position position = LSPUtils.logicalToLSPPos(editor.offsetToLogicalPosition(offset));
            final TextDocumentPositionParams textDocumentPositionParams = new TextDocumentPositionParams(textDocument, position);
            final CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion = dwTextDocumentService.completion(textDocumentPositionParams);
            final Either<List<CompletionItem>, CompletionList> listCompletionListEither = completion.get();
            if (listCompletionListEither.isLeft()) {
                return createElements(listCompletionListEither.getLeft());
            } else {
                CompletionList right = listCompletionListEither.getRight();
                return createElements(right.getItems());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<LookupElement> createElements(List<CompletionItem> items) {
        ArrayList<LookupElement> result = new ArrayList<>();
        for (CompletionItem item : items) {
            result.add(createLookupItem(item));
        }
        return result;
    }

    private LookupElement createLookupItem(CompletionItem item) {
        final List<TextEdit> addTextEdits = item.getAdditionalTextEdits();
        final Command command = item.getCommand();
        final Object data = item.getData();
        final String detail = item.getDetail();
        final String doc = item.getDocumentation();
        final String filterText = item.getFilterText();
        final String insertText = item.getInsertText();
        final InsertTextFormat insertFormat = item.getInsertTextFormat();
        final CompletionItemKind kind = item.getKind();
        final String label = item.getLabel();
        final TextEdit textEdit = item.getTextEdit();
        final String sortText = item.getSortText();
        final String presentableText = (label != null && !Objects.equals(label, "")) ? label : (insertText != null) ? insertText : "";
        final String tailText = (detail != null) ? detail : "";
        final Icon icon = getCompletionIcon(kind);
        final LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(insertText);
        if (kind == CompletionItemKind.Keyword) {
            lookupElementBuilder.withBoldness(true);
        }

        return lookupElementBuilder
                .withPresentableText(presentableText)
                .withTailText(tailText, true)
                .withIcon(icon)
                .withAutoCompletionPolicy(AutoCompletionPolicy.SETTINGS_DEPENDENT);
    }

    private Icon getCompletionIcon(CompletionItemKind kind) {
        switch (kind) {
            case Class:
                return AllIcons.Nodes.Class;
            case Color:
                return null;
            case Constructor:
                return null;
            case Enum:
                return AllIcons.Nodes.Enum;
            case Field:
                return AllIcons.Nodes.Field;
            case File:
                return AllIcons.FileTypes.Any_type;
            case Function:
                return AllIcons.Nodes.Function;
            case Interface:
                return AllIcons.Nodes.Interface;
            case Keyword:
                return null;
            case Method:
                return AllIcons.Nodes.Method;
            case Module:
                return AllIcons.Nodes.Module;
            case Property:
                return AllIcons.Nodes.Property;
            case Reference:
                return AllIcons.Nodes.MethodReference;
            case Snippet:
                return null;
            case Text:
                return AllIcons.FileTypes.Text;
            case Unit:
                return null;
            case Value:
                return null;
            case Variable:
                return AllIcons.Nodes.Variable;
            default:
                return null;
        }
    }

    public static void invokeLater(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable);
    }

    public static DataWeaveServiceManager getInstance(@NotNull Project project) {
        return project.getComponent(DataWeaveServiceManager.class);
    }

}
