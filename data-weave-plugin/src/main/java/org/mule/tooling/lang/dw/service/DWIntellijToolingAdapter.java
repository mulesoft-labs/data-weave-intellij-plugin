package org.mule.tooling.lang.dw.service;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.qn.WeaveQualifiedNameProvider;
import org.mule.weave.v2.completion.EmptyDataFormatDescriptorProvider$;
import org.mule.weave.v2.completion.Suggestion;
import org.mule.weave.v2.completion.SuggestionType;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.editor.VirtualFile;
import org.mule.weave.v2.editor.WeaveDocumentToolingService;
import org.mule.weave.v2.editor.WeaveToolingService;
import org.mule.weave.v2.hover.HoverMessage;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.scope.Reference;
import org.mule.weave.v2.ts.WeaveType;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

public class DWIntellijToolingAdapter extends AbstractProjectComponent {

    private IntellijVirtualFileSystemAdaptor projectVirtualFileSystem;
    private WeaveToolingService dwTextDocumentService;

    protected DWIntellijToolingAdapter(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        projectVirtualFileSystem = new IntellijVirtualFileSystemAdaptor(myProject);
        dwTextDocumentService = new WeaveToolingService(projectVirtualFileSystem, EmptyDataFormatDescriptorProvider$.MODULE$);
    }

    public List<LookupElement> completion(CompletionParameters completionParameters) {
        //First make sure is in the write context
        final Document document = completionParameters.getEditor().getDocument();
        final WeaveDocumentToolingService weaveDocumentService = didOpen(document);
        final int offset = completionParameters.getOffset();
        final Suggestion[] items = weaveDocumentService.completionItems(offset);
        return createElements(items);
    }

    private WeaveDocumentToolingService didOpen(Document document) {
        final PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(document);
        assert psiFile != null;
        return didOpen(psiFile);
    }

    public WeaveType parseType(String weaveType) {
        Option<WeaveType> weaveTypeOption = dwTextDocumentService.parseType(weaveType);
        if (weaveTypeOption.isDefined()) {
            return weaveTypeOption.get();
        } else {
            return null;
        }
    }

    private WeaveDocumentToolingService didOpen(PsiFile psiFile) {
        final String url = psiFile.getVirtualFile().getUrl();
        final VirtualFile file = projectVirtualFileSystem.file(url);
        final DataWeaveScenariosManager instance = DataWeaveScenariosManager.getInstance(myProject);
        final WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
        final ImplicitInput currentImplicitTypes = instance.getCurrentImplicitTypes(weaveDocument);
        final WeaveType expectedOutput = instance.getExpectedOutput(weaveDocument);
        return dwTextDocumentService.open(file, currentImplicitTypes != null ? currentImplicitTypes : new ImplicitInput(), Option.apply(expectedOutput));
    }

    @Nullable
    public String hover(PsiElement element) {
        Document document = PsiDocumentManager.getInstance(myProject).getDocument(element.getContainingFile());
        WeaveDocumentToolingService weaveDocumentService = didOpen(document);
        Option<HoverMessage> hoverResult = weaveDocumentService.hoverResult(element.getTextOffset());
        if (hoverResult.isDefined()) {
            HoverMessage hoverMessage = hoverResult.get();
            String expressionString = hoverMessage.resultType().toString(true, true);
            return toHtml("*Expression type* : `" + expressionString + "`");
        } else {
            return null;
        }
    }

    @Nullable
    public PsiElement resolveReference(WeaveIdentifier identifier) {
        final PsiFile containerFile = identifier.getContainingFile();
        final WeaveDocumentToolingService weaveDocumentService = didOpen(containerFile);
        final Option<Reference> referenceOption = weaveDocumentService.definition(identifier.getTextOffset());
        if (referenceOption.isDefined()) {
            final Reference reference = referenceOption.get();
            return resolveReference(reference, containerFile);
        } else {
            return null;
        }
    }

    @Nullable
    private PsiElement resolveReference(Reference reference, PsiFile containerFile) {
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
            container = containerFile;
        }
        final NameIdentifier referencedNode = reference.referencedNode();
        final PsiElement elementAtOffset = PsiUtil.getElementAtOffset(container, referencedNode.location().startPosition().index());
        //We should link the NamedElement on the other side.
        return PsiTreeUtil.getParentOfType(elementAtOffset, WeaveNamedElement.class);
    }


    private List<LookupElement> createElements(Suggestion[] items) {
        ArrayList<LookupElement> result = new ArrayList<>();
        for (Suggestion item : items) {
            result.add(createLookupItem(item));
        }
        return result;
    }

    private LookupElement createLookupItem(Suggestion item) {

        LookupElementBuilder elementBuilder;
        Option<String> documentationMayBe = item.markdownDocumentation();
        String documentation = null;
        if (documentationMayBe.isDefined()) {
            documentation = documentationMayBe.get();
        }
        elementBuilder = LookupElementBuilder.create(new CompletionData(item.name(), documentation));
        elementBuilder = elementBuilder.withPresentableText(item.name());


        int itemType = item.itemType();
        if (itemType == SuggestionType.Class()) {
            elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Class);
        } else if (itemType == SuggestionType.Variable()) {
            elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Variable);
        } else if (itemType == SuggestionType.Field()) {
            elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Field);
        } else if (itemType == SuggestionType.Function()) {
            elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Function);
        } else if (itemType == SuggestionType.Keyword()) {
            elementBuilder = elementBuilder.bold();
        }

        if (item.wtype().isDefined()) {
            elementBuilder = elementBuilder.withTypeText(item.wtype().get().toString(false, true));
        }
        return elementBuilder.withAutoCompletionPolicy(AutoCompletionPolicy.SETTINGS_DEPENDENT);
    }

    public static DWIntellijToolingAdapter getInstance(@NotNull Project project) {
        return project.getComponent(DWIntellijToolingAdapter.class);
    }

    @Nullable
    public String documentation(PsiElement psiElement) {
        WeaveDocumentToolingService weaveDocumentService = didOpen(psiElement.getContainingFile());
        Option<HoverMessage> hoverMessageOption = weaveDocumentService.hoverResult(psiElement.getTextOffset());
        String result = null;
        if (hoverMessageOption.isDefined()) {
            HoverMessage hoverMessage = hoverMessageOption.get();
            Option<String> documentation = hoverMessage.markdownDocs();
            if (documentation.isDefined()) {
                result = toHtml(hoverMessage.markdownDocs().get());
            }
        }
        return result;
    }

    @Nullable
    public static String toHtml(@Nullable String text) {
        if (text == null) {
            return null;
        }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CompletionData that = (CompletionData) o;

            return label != null ? label.equals(that.label) : that.label == null;
        }

        @Override
        public int hashCode() {
            return label != null ? label.hashCode() : 0;
        }
    }
}
