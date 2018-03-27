package org.mule.tooling.lang.dw.service;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtil;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.qn.WeaveQualifiedNameProvider;
import org.mule.weave.lsp.DWTextDocumentService;
import org.mule.weave.lsp.WeaveDocumentService;
import org.mule.weave.v2.completion.EmptyDataFormatDescriptorProvider$;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.scope.Reference;
import scala.Option;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        //First make sure is in the write context
        final Document document = completionParameters.getEditor().getDocument();
        final WeaveDocumentService weaveDocumentService = didOpen(document);
        final int offset = completionParameters.getOffset();
        final CompletionList listCompletionListEither = weaveDocumentService.completion(offset);
        return createElements(listCompletionListEither.getItems());
    }

    public WeaveDocumentService didOpen(Document document) {
        final PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(document);
        return didOpen(psiFile);
    }

    private WeaveDocumentService didOpen(PsiFile psiFile) {
        final String url = psiFile.getVirtualFile().getUrl();
        return dwTextDocumentService.open(url);
    }

    @Nullable
    public String hover(PsiElement element) {
        Document document = PsiDocumentManager.getInstance(myProject).getDocument(element.getContainingFile());
        WeaveDocumentService weaveDocumentService = didOpen(document);
        Hover hover = weaveDocumentService.hover(element.getTextOffset());
        if (hover != null) {
            List<Either<String, MarkedString>> contents = hover.getContents();
            if (!contents.isEmpty()) {
                Either<String, MarkedString> stringEither = contents.get(0);
                if (stringEither.isLeft()) {
                    return stringEither.getLeft();
                } else {
                    return stringEither.getRight().getValue();
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public PsiElement resolveReference(WeaveIdentifier identifier) {
        final WeaveDocumentService weaveDocumentService = didOpen(identifier.getContainingFile());
        final Option<Reference> referenceOption = weaveDocumentService.resolveReference(identifier.getTextOffset());
        if (referenceOption.isDefined()) {
            final Reference reference = referenceOption.get();
            final Option<NameIdentifier> nameIdentifier = reference.moduleSource();
            final WeaveQualifiedNameProvider nameProvider = new WeaveQualifiedNameProvider();
            PsiFile container;
            if (nameIdentifier.isDefined()) {
                PsiElement psiElement = nameProvider.getPsiElement(myProject, nameIdentifier.get());
                if (psiElement != null) {
                    container = psiElement.getContainingFile();
                } else {
                    //Unable to find the module
                    return null;
                }
            } else {
                container = identifier.getContainingFile();
            }
            NameIdentifier referencedNode = reference.referencedNode();
            return PsiUtil.getElementAtOffset(container, referencedNode.location().startPosition().index());
        } else {
            return null;
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
        final LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(label);
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

    @Nullable
    public String documentation(PsiElement psiElement) {
        WeaveDocumentService weaveDocumentService = didOpen(psiElement.getContainingFile());
        SignatureHelp signatureHelp = weaveDocumentService.documentationOf(psiElement.getTextOffset());
        List<SignatureInformation> signatures = signatureHelp.getSignatures();
        if (!signatures.isEmpty()) {
            String documentation = signatures.get(0).getDocumentation();
            if (documentation != null) {
                return toHtml(documentation);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String toHtml(String text) {
        GFMFlavourDescriptor flavour = new GFMFlavourDescriptor();
        ASTNode astNode = new MarkdownParser(flavour).buildMarkdownTreeFromString(text);
        return new HtmlGenerator(text, astNode, flavour, true).generateHtml();
    }

    public static class CompletionData {
        private String label;
        private String documentation;

        public CompletionData(String label, String documentation) {
            this.label = label;
            this.documentation = documentation;
        }

        public String getLabel() {
            return label;
        }

        public String getDocumentation() {
            return documentation;
        }

        public String toString() {
            return label;
        }
    }
}
