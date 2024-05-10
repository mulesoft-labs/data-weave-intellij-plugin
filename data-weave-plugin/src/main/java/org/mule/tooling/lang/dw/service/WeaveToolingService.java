package org.mule.tooling.lang.dw.service;

import com.intellij.ProjectTopics;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.compiler.server.BuildManagerListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveIcons;
import org.mule.tooling.lang.dw.indexer.IJDataWeaveIndexerService;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.qn.WeaveQualifiedNameProvider;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentService;
import org.mule.tooling.lang.dw.util.AsyncCache;
import org.mule.weave.v2.agent.api.event.WeaveDataFormatDescriptor;
import org.mule.weave.v2.agent.api.event.WeaveDataFormatProperty;
import org.mule.weave.v2.completion.DataFormatProperty;
import org.mule.weave.v2.completion.*;
import org.mule.weave.v2.editor.*;
import org.mule.weave.v2.hover.HoverMessage;
import org.mule.weave.v2.parser.ast.AstNode;
import org.mule.weave.v2.parser.ast.header.directives.FunctionDirectiveNode;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.scope.Reference;
import org.mule.weave.v2.scope.VariableDependency;
import org.mule.weave.v2.scope.VariableScope;
import org.mule.weave.v2.sdk.WeaveResource;
import org.mule.weave.v2.sdk.WeaveResource$;
import org.mule.weave.v2.sdk.WeaveResourceResolver;
import org.mule.weave.v2.ts.WeaveType;
import scala.Option;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(Service.Level.PROJECT)
public final class WeaveToolingService implements Disposable {

    public static final String RAML = "raml";
    public static final String JAVA = "java";
    private IJVirtualFileSystemAdaptor projectVirtualFileSystem;
    private org.mule.weave.v2.editor.WeaveToolingService dwTextDocumentService;

    final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    private Project myProject;

    public static WeaveToolingService getInstance(@NotNull Project project) {
        return project.getService(WeaveToolingService.class);
    }

    private WeaveToolingService(Project project) {
        this.myProject = project;
        initComponent();
    }

    public void initComponent() {
        final IJDataWeaveIndexerService indexer = new IJDataWeaveIndexerService(myProject);
        this.projectVirtualFileSystem = new IJVirtualFileSystemAdaptor(myProject);
        final RemoteResourceResolver javaRemoteResolver = new RemoteResourceResolver(myProject);
        final RemoteResourceResolver ramlRemoteResolver = new RemoteResourceResolver(myProject);
        final ModuleLoaderFactory[] moduleResourceResolvers = {
                SpecificModuleResourceResolver.apply(JAVA, javaRemoteResolver),
                SpecificModuleResourceResolver.apply(RAML, ramlRemoteResolver)
        };

        final AsyncDataFormatProvider dataFormatProvider = new AsyncDataFormatProvider(myProject);
        dwTextDocumentService = new org.mule.weave.v2.editor.WeaveToolingService(projectVirtualFileSystem, dataFormatProvider, moduleResourceResolvers)
                .withSymbolsIndexService(indexer);
        projectVirtualFileSystem.changeListener(new ChangeListener() {

            private void invalidate(VirtualFile file) {
                javaRemoteResolver.invalidateCache(file.getNameIdentifier());
                ramlRemoteResolver.invalidateCache(file.getNameIdentifier());
            }

            @Override
            public void onDeleted(VirtualFile file) {
                invalidate(file);
            }

            @Override
            public void onCreated(VirtualFile file) {
                invalidate(file);
            }

            @Override
            public void onChanged(VirtualFile file) {
                invalidate(file);
            }
        });

        myProject.getMessageBus()
                .connect(this)
                .subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
                    @Override
                    public void rootsChanged(@NotNull ModuleRootEvent event) {
                        System.out.println("WeaveToolingService.rootsChanged");
                        ReadAction.nonBlocking(() -> {
                                    dwTextDocumentService.invalidateAll();
                                    dataFormatProvider.scheduleLoadDataFormats();
                                    return null;
                                })
                                .submit(AppExecutorUtil.getAppExecutorService());
                    }
                });

        final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        final MessageBusConnection connection = messageBus.connect(this);
        connection.subscribe(BuildManagerListener.TOPIC, new BuildManagerListener() {
            @Override
            public void buildFinished(@NotNull Project project, @NotNull UUID sessionId, boolean isAutomake) {
                if (project == myProject) {
                    ReadAction.nonBlocking(() -> {
                                dataFormatProvider.scheduleLoadDataFormats();
                                return null;
                            })
                            .submit(AppExecutorUtil.getAppExecutorService());
                }
            }
        });
    }


    public List<LookupElement> completion(CompletionParameters completionParameters) {
        //First make sure is in the write context
//        PsiElement context = completionParameters.getOriginalFile().getContext();
        final Document document = completionParameters.getEditor().getDocument();
        final WeaveDocumentToolingService weaveDocumentService = didOpen(document, true);
        final int offset = completionParameters.getOffset();
        final Suggestion[] items = weaveDocumentService.completionItems(offset);
        return createElements(items);
    }

    public ValidationMessages typeCheck(PsiFile file) {
        return ReadAction.compute(() -> {
            return didOpen(file, false).typeCheck();
        });
    }

    public ValidationMessages parseCheck(PsiFile file) {
        return ReadAction.compute(() -> {
            return didOpen(file, false).parseCheck();
        });
    }

    private WeaveDocumentToolingService didOpen(Document document, boolean useExpectedOutput) {
        final PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(document);
        assert psiFile != null;
        return didOpen(psiFile, useExpectedOutput);
    }

    public WeaveType parseType(String weaveType) {
        final Option<WeaveType> weaveTypeOption = dwTextDocumentService.loadType(weaveType);
        if (weaveTypeOption.isDefined()) {
            return weaveTypeOption.get();
        } else {
            return null;
        }
    }

    @Nullable
    public WeaveType typeOf(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return null;
        } else {
            final WeaveDocumentToolingService weaveDocument = didOpen(element.getContainingFile(), false);
            final TextRange textRange = element.getTextRange();
            return weaveDocument.typeOf(textRange.getStartOffset(), textRange.getEndOffset());
        }
    }

    @Nullable
    public WeaveType typeOf(String element, ImplicitInput inputs) {
        final VirtualFile file = new IJVirtualFileSystemAdaptor.IJInMemoryFileAdaptor(element, projectVirtualFileSystem);
        final WeaveDocumentToolingService weaveDocumentToolingService = dwTextDocumentService.openInMemory(file, inputs, Option.empty());
        final Option<WeaveType> weaveTypeOption = weaveDocumentToolingService.typeOfMapping();
        if (weaveTypeOption.isDefined()) {
            return weaveTypeOption.get();
        } else {
            return null;
        }
    }

    public Optional<String> scaffoldWeaveDocOf(@NotNull PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return Optional.empty();
        }
        final WeaveDocumentToolingService weaveDocument = didOpen(containingFile, false);
        final TextRange textRange = element.getTextRange();
        Option<String> stringOption = weaveDocument.scaffoldDocs(textRange.getStartOffset(), textRange.getEndOffset());
        if (stringOption.isDefined()) {
            return Optional.of(stringOption.get());
        } else {
            return Optional.empty();
        }
    }

    public ValidationMessages weaveDocCheck(PsiFile file) {
        return didOpen(file, false).validateDocs();
    }

    private WeaveDocumentToolingService didOpen(@NotNull PsiFile psiFile, boolean useExpectedOutput) {
        final Optional<InputOutputTypesProvider> inputOutputTypesProvider = ReadAction.compute(() -> InputOutputTypesExtensionService.inputOutputTypesProvider(psiFile));
        final WeaveRuntimeService instance = WeaveRuntimeService.getInstance(myProject);
        final ImplicitInput currentImplicitTypes;
        final WeaveType expectedOutput;
        if (inputOutputTypesProvider.isEmpty()) {
            final WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(psiFile));
            currentImplicitTypes = weaveDocument != null ? instance.getImplicitInputTypes(weaveDocument) : null;
            expectedOutput = useExpectedOutput && weaveDocument != null ? instance.getExpectedOutput(weaveDocument) : null;
        } else {
            currentImplicitTypes = ReadAction.compute(() -> inputOutputTypesProvider.get().inputTypes(psiFile));
            expectedOutput = useExpectedOutput ? ReadAction.compute(() -> inputOutputTypesProvider.get().expectedOutput(psiFile)).orElse(null) : null;
        }

        return ReadAction.compute(() -> {
            com.intellij.openapi.vfs.VirtualFile virtualFile = psiFile.getVirtualFile();
            final VirtualFile file;
            if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
                file = new IJVirtualFileSystemAdaptor.IJInMemoryFileAdaptor(psiFile.getText(), projectVirtualFileSystem);
            } else {
                final String url = virtualFile.getUrl();
                file = projectVirtualFileSystem.file(url);
            }
            final Option<WeaveType> apply = Option.apply(expectedOutput);
            if (virtualFile != null && virtualFile.isInLocalFileSystem()) {
                ImplicitInput implicitInput = currentImplicitTypes != null ? currentImplicitTypes : new ImplicitInput();
                return dwTextDocumentService.open(file, implicitInput, apply);
            } else {
                return dwTextDocumentService.openInMemory(file, currentImplicitTypes != null ? currentImplicitTypes : new ImplicitInput(), apply);
            }
        });
    }

    @Nullable
    public String hover(PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            final WeaveDocumentToolingService weaveDocumentService = didOpen(containingFile, false);
            final Option<HoverMessage> hoverResult = weaveDocumentService.hoverResult(element.getTextOffset());
            if (hoverResult.isDefined()) {
                final HoverMessage hoverMessage = hoverResult.get();
                final String expressionString = hoverMessage.resultType().toString(true, true);
                return toHtml("*Expression type* : `" + expressionString + "`");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @NotNull
    public PsiElement[] resolveReference(WeaveIdentifier identifier) {
        final PsiFile containerFile = identifier.getContainingFile();
        final WeaveDocumentToolingService weaveDocumentService = didOpen(containerFile, false);
        final Link[] referenceOption = weaveDocumentService.definitions(identifier.getTextOffset());
        return Arrays.stream(referenceOption)
                .map((reference) -> {
                    return resolveReference(reference.reference(), containerFile);
                })
                .filter((mr) -> mr.isPresent())
                .map((mr) -> mr.get())
                .toArray(PsiElement[]::new);

    }

    private Optional<PsiElement> resolveReference(Reference reference, PsiFile containerFile) {
        PsiElement result = null;
        final Option<NameIdentifier> nameIdentifier = reference.moduleSource();
        final WeaveQualifiedNameProvider nameProvider = new WeaveQualifiedNameProvider();
        PsiFile container;
        if (nameIdentifier.isDefined()) {
            PsiElement psiElement = nameProvider.getPsiElement(myProject, nameIdentifier.get());
            if (psiElement != null) {
                container = psiElement.getContainingFile();
                if (nameIdentifier.get().loader().isDefined()) {
                    //When having custom module loader just navegate to the file
                    result = psiElement;
                }
            } else {
                //Unable to find the module
                return Optional.empty();
            }
        } else {
            container = containerFile;
        }

        if (result == null) {
            final NameIdentifier referencedNode = reference.referencedNode();
            final Option<FunctionDirectiveNode> mayBeDirective = reference.scope().astNavigator().parentWithType(referencedNode, FunctionDirectiveNode.class);
            final WeaveNamedElement parentOfType = getWeaveNamedElement(container, referencedNode);
            if (parentOfType != null) {
                result = parentOfType;
            }
        }

        return Optional.ofNullable(result);

    }

    private WeaveNamedElement getWeaveNamedElement(PsiFile container, AstNode referencedNode) {
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


        final Option<String> documentationMayBe = item.markdownDocumentation();
        String documentation = null;
        if (documentationMayBe.isDefined()) {
            documentation = documentationMayBe.get();
        }
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(new CompletionData(item.name(), documentation));
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
        } else if (itemType == SuggestionType.Module()) {
            elementBuilder = elementBuilder.withIcon(WeaveIcons.DataWeaveModuleIcon);
        } else if (itemType == SuggestionType.Keyword()) {
            elementBuilder = elementBuilder.bold();
        }


        org.mule.weave.v2.completion.Template template = item.template();
        final Template myTemplate = IJAdapterHelper.toIJTemplate(myProject, template);

        elementBuilder = elementBuilder.withInsertHandler((context, item1) -> {

            final int selectionStart = context.getEditor().getCaretModel().getOffset();
            final int startOffset = context.getStartOffset();
            final int tailOffset = context.getTailOffset();
            context.getDocument().deleteString(startOffset, tailOffset);
            context.setAddCompletionChar(false);
            TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate, new TemplateEditingAdapter() {

                @Override
                public void templateFinished(@NotNull Template template, boolean brokenOff) {
                    int length = context.getEditor().getDocument().getText().length();
                    if (item.insertAction().length > 0) {
                        final QuickFixAction[] insertAction = item.insertAction();
                        for (QuickFixAction quickFixAction : insertAction) {
                            quickFixAction.run(new IJWeaveTextDocument(context.getEditor(), context.getProject()));
                        }
                        final int newLength = context.getEditor().getDocument().getText().length();
                        context.getEditor().getCaretModel().moveToOffset(selectionStart + newLength - length);
                    }
                }
            });


        });

        if (item.wtype().isDefined()) {
            elementBuilder = elementBuilder.withTypeText(item.wtype().get().toString(false, true));
        }
        return elementBuilder;
    }


    @Nullable
    public String documentation(PsiElement psiElement) {
        final PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile == null) {
            return null;
        }
        final WeaveDocumentToolingService weaveDocumentService = didOpen(containingFile, false);
        final Option<HoverMessage> hoverMessageOption = weaveDocumentService.hoverResult(psiElement.getTextOffset());
        String result = null;
        if (hoverMessageOption.isDefined()) {
            HoverMessage hoverMessage = hoverMessageOption.get();
            Option<String> documentation = hoverMessage.markdownDocs();
            if (documentation.isDefined()) {
                result = toHtml(documentation.get());
            }
        }
        return result;
    }

    @Nullable
    public PsiElement scopeOf(PsiFile file, PsiElement element) {
        VariableScope variableScope = scopeOf(element);
        if (variableScope != null) {
            AstNode astNode = variableScope.astNode();
            if (astNode instanceof WeaveDocument) {
                return WeavePsiUtils.getWeaveDocument(file);
            } else {
                return WeavePsiUtils.findInnerElementRange(file, astNode.location().startPosition().index(), astNode.location().endPosition().index());
            }
        } else {
            return null;
        }
    }


    @Nullable
    public static String toHtml(@Nullable String text) {
        if (text == null) {
            return null;
        }
        List<Extension> extensions = List.of(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        return renderer.render(document);
    }

    @Nullable
    public VariableScope scopeOf(PsiElement element) {
        return ReadAction.compute(() -> {
            final PsiFile containingFile = element.getContainingFile();
            if (containingFile == null) {
                return null;
            }
            WeaveDocumentToolingService weaveDocumentToolingService = didOpen(containingFile, false);
            Option<VariableScope> scopeOf = weaveDocumentToolingService.scopeOf(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            if (scopeOf.isDefined()) {
                return scopeOf.get();
            } else {
                return null;
            }
        });
    }

    public VariableDependency[] externalScopeDependencies(PsiElement element, @Nullable VariableScope parent) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return new VariableDependency[0];
        }
        WeaveDocumentToolingService weaveDocumentToolingService = didOpen(containingFile, false);
        TextRange textRange = element.getTextRange();
        return weaveDocumentToolingService.externalScopeDependencies(textRange.getStartOffset(), textRange.getEndOffset(), Option.apply(parent));
    }

    @Override
    public void dispose() {
        Disposer.dispose(projectVirtualFileSystem);
    }

    public void reformat(Document document) {
        Option<ReformatResult> formatting = didOpen(document, false).formatting();
        if (formatting.isDefined()) {
            ApplicationManager.getApplication().runWriteAction(() -> document.setText(formatting.get().newFormat()));
        }
    }

    @Nullable
    public String typeOf(Document document, int selectionStart, int selectionEnd) {
        WeaveType weaveType = didOpen(document, false).typeOf(selectionStart, selectionEnd);
        if (weaveType != null) {
            return weaveType.toString(true, true);
        } else {
            return null;
        }
    }

    @Nullable
    public String astString(PsiFile selectedFile) {
        Option<String> stringOption = didOpen(selectedFile, false).astString();
        if (stringOption.isDefined()) {
            return stringOption.get();
        } else {
            return null;
        }
    }


    @Nullable
    public String typeGraphString(PsiFile selectedFile) {
        Option<String> stringOption = didOpen(selectedFile, false).typeGraphString();
        if (stringOption.isDefined()) {
            return stringOption.get();
        } else {
            return null;
        }
    }

    @Nullable
    public String scopeGraphString(PsiFile selectedFile) {
        Option<String> stringOption = didOpen(selectedFile, false).scopeGraphString();
        if (stringOption.isDefined()) {
            return stringOption.get();
        } else {
            return null;
        }
    }

    public String dependencyGraph() {
        return dwTextDocumentService.dependencyGraphString();
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

            return Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return label != null ? label.hashCode() : 0;
        }
    }

    private class RemoteResourceResolver implements WeaveResourceResolver {

        private Project myProject;
        private AsyncCache<NameIdentifier, Option<WeaveResource>> cache = new AsyncCache<NameIdentifier, Option<WeaveResource>>((name, callback) ->
                WeaveAgentService.getInstance(myProject)
                        .resolveModule(name.name(), name.loader().get(), myProject, event -> {
                            if (event.content().isDefined()) {
                                String content = event.content().get();
                                Option<WeaveResource> resourceOption = Option.<WeaveResource>apply(WeaveResource$.MODULE$.apply(name.name(), content));
                                callback.accept(resourceOption);
                                dwTextDocumentService.invalidateModule(name);
                            } else {
                                callback.accept(Option.empty());
                            }
                        })
        )
                .withTimeOut(WeaveConstants.SERVER_TIMEOUT * 5);

        public RemoteResourceResolver(Project myProject) {
            this.myProject = myProject;
        }

        void invalidateCache(NameIdentifier name) {
            cache.invalidate(name);
        }

        @Override
        public Option<WeaveResource> resolve(NameIdentifier name) {
            return cache.resolve(name).orElse(Option.empty());
        }
    }


    private static class AsyncDataFormatProvider implements DataFormatDescriptorProvider, Disposable {
        private final Alarm myRestartAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
        private DataFormatDescriptor[] formats = new DataFormatDescriptor[0];
        private Project project;

        public AsyncDataFormatProvider(Project project) {
            this.project = project;
            Disposer.register(project, this);
            loadDataFormats();
        }

        public void scheduleLoadDataFormats() {
            myRestartAlarm.cancelAllRequests();
            myRestartAlarm.addRequest(() -> {
                //We wait for one second to see if new reloads come
                loadDataFormats();
            }, TimeUnit.SECONDS.toMillis(1));
        }

        private void loadDataFormats() {
            ProgressManager.getInstance().run(
                    new Task.Backgroundable(project, "Loading data formats") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            doLoadDataFormats();
                        }
                    });
        }

        private void doLoadDataFormats() {
            WeaveRuntimeService weaveRuntimeService = WeaveRuntimeService.getInstance(project);
            weaveRuntimeService.availableDataFormat((dataFormatDescriptor) -> {
                final List<DataFormatDescriptor> descriptors = new ArrayList<>();
                for (WeaveDataFormatDescriptor weaveDataFormatDescriptor : dataFormatDescriptor) {
                    final String mimeType = weaveDataFormatDescriptor.mimeType();
                    final DataFormatDescriptor descriptor = DataFormatDescriptor.apply(mimeType, weaveDataFormatDescriptor.id(), toDataFormatProp(weaveDataFormatDescriptor.writerProperties()), toDataFormatProp(weaveDataFormatDescriptor.readerProperties()));
                    descriptors.add(descriptor);
                }
                formats = descriptors.toArray(new DataFormatDescriptor[0]);
            });
        }

        @NotNull
        public DataFormatProperty[] toDataFormatProp(WeaveDataFormatProperty[] weaveDataFormatPropertySeq) {
            final List<DataFormatProperty> properties = new ArrayList<>();
            for (WeaveDataFormatProperty property : weaveDataFormatPropertySeq) {
                properties.add(DataFormatProperty.apply(property.name(), property.description(), property.wtype(), property.values()));
            }
            return properties.toArray(new DataFormatProperty[0]);
        }


        @Override
        public DataFormatDescriptor[] dataFormats() {
            return formats;
        }

        @Override
        public void dispose() {

        }
    }

}
