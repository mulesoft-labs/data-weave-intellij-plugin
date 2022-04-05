package org.mule.tooling.als.component;

import amf.core.client.common.remote.Content;
import amf.core.client.platform.resource.ClientResourceLoader;
import amf.core.client.platform.resource.ResourceNotFound;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.als.settings.DialectsRegistry;
import org.mule.tooling.als.utils.LSPUtils;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mulesoft.als.configuration.AlsConfiguration;
import org.mulesoft.als.logger.PrintLnLogger$;
import org.mulesoft.als.server.EmptyJvmSerializationProps$;
import org.mulesoft.als.server.client.platform.AlsLanguageServerFactory;
import org.mulesoft.als.server.client.platform.ClientNotifier;
import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeClientCapabilities;
import org.mulesoft.als.server.feature.fileusage.FileUsageClientCapabilities;
import org.mulesoft.als.server.feature.renamefile.RenameFileActionClientCapabilities;
import org.mulesoft.als.server.feature.serialization.ConversionClientCapabilities;
import org.mulesoft.als.server.feature.serialization.SerializationClientCapabilities;
import org.mulesoft.als.server.protocol.LanguageServer;
import org.mulesoft.als.server.protocol.configuration.AlsClientCapabilities;
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams;
import org.mulesoft.als.server.protocol.configuration.AlsInitializeResult;
import org.mulesoft.als.server.workspace.command.Commands;
import org.mulesoft.lsp.configuration.*;
import org.mulesoft.lsp.edit.InsertReplaceEdit;
import org.mulesoft.lsp.edit.TextEdit;
import org.mulesoft.lsp.feature.RequestHandler;
import org.mulesoft.lsp.feature.codeactions.CodeActionCapabilities;
import org.mulesoft.lsp.feature.common.Range;
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier;
import org.mulesoft.lsp.feature.common.TextDocumentItem;
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier;
import org.mulesoft.lsp.feature.completion.*;
import org.mulesoft.lsp.feature.definition.DefinitionClientCapabilities;
import org.mulesoft.lsp.feature.diagnostic.Diagnostic;
import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities;
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams;
import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingClientCapabilities;
import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingClientCapabilities;
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolClientCapabilities;
import org.mulesoft.lsp.feature.folding.FoldingRangeCapabilities;
import org.mulesoft.lsp.feature.highlight.DocumentHighlightCapabilities;
import org.mulesoft.lsp.feature.hover.Hover;
import org.mulesoft.lsp.feature.hover.HoverClientCapabilities;
import org.mulesoft.lsp.feature.hover.HoverParams;
import org.mulesoft.lsp.feature.hover.HoverRequestType$;
import org.mulesoft.lsp.feature.implementation.ImplementationClientCapabilities;
import org.mulesoft.lsp.feature.link.DocumentLinkClientCapabilities;
import org.mulesoft.lsp.feature.reference.ReferenceClientCapabilities;
import org.mulesoft.lsp.feature.rename.RenameClientCapabilities;
import org.mulesoft.lsp.feature.selectionRange.SelectionRangeCapabilities;
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage;
import org.mulesoft.lsp.feature.typedefinition.TypeDefinitionClientCapabilities;
import org.mulesoft.lsp.textsync.*;
import org.mulesoft.lsp.workspace.ExecuteCommandParams;
import scala.Enumeration;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.concurrent.Future;
import scala.util.Either;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.mule.tooling.als.utils.LSPUtils.*;
import static org.mule.tooling.lang.dw.util.ScalaUtils.toOptional;
import static scala.collection.JavaConverters.asJavaCollection;

public class ALSLanguageService implements Disposable {

  static Pattern VARIABLES = Pattern.compile("\\$([0-9]+)");


  private final Project myProject;
  private LanguageServer languageServer;
  private Map<String, DocumentState> documents = new HashMap<String, DocumentState>();
  private ALSLanguageExtension[] supportedLanguages;
  private List<ALSLanguageExtension.Dialect> dialectByExtensionPoint;
  private final Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);

  public ALSLanguageService(Project project) {
    myProject = project;
    init();
  }

  public static ALSLanguageService getInstance(Project project) {
    return ServiceManager.getService(project, ALSLanguageService.class);
  }


  public void init() {
    ApplicationManager.getApplication().getMessageBus().connect().subscribe(DialectsRegistry.DialectAddedNotifier.CHANGE_ACTION_TOPIC, new DialectsRegistry.DialectAddedNotifier() {
      @Override
      public void dialectAdded(List<DialectsRegistry.DialectLocation> context) {
        registerUserDefinedDialects();
      }
    });
    supportedLanguages = ALSLanguageExtensionService.languages();
    dialectByExtensionPoint = Arrays.stream(supportedLanguages)
            .map((l) -> l.customDialect(myProject))
            .filter((l) -> l.isPresent())
            .map((l) -> l.get())
            .collect(Collectors.toList());
    PsiManager.getInstance(this.myProject).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
      @Override
      protected void onChange(@Nullable PsiFile file) {
        if (file != null && wasFileOpened(file)) {
          onChanged(file);
        }
      }
    }, this);

    VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {

      @Override
      public void contentsChanged(@NotNull VirtualFileEvent event) {
        updateDialectIfNeeded(event);
      }

      private void updateDialectIfNeeded(VirtualFileEvent event) {
        //Only yaml files
        if (!"yaml".equalsIgnoreCase(event.getFile().getExtension())) {
          return;
        }

        String url = event.getFile().getUrl();
        List<DialectsRegistry.DialectLocation> dialectsRegistry = DialectsRegistry.getInstance().getDialectsRegistry();
        Optional<DialectsRegistry.DialectLocation> first =
                dialectsRegistry.stream().filter((d) -> {
                  final String fileUrl = LSPUtils.toLSPUrl(d.getDialectFilePath());

                  return url.equals(fileUrl);
                }).findFirst();
        if (first.isPresent()) {
          DialectsRegistry.DialectLocation dialectLocation = first.get();
          Optional<ALSLanguageExtension.Dialect> dialect = new UserDialectLanguageExtension(dialectLocation.getName(), dialectLocation.getDialectFilePath()).customDialect(myProject);
          dialect.ifPresent(value -> {
            scheduleUpdateDialect(event, value);
          });
        }

        Optional<ALSLanguageExtension.Dialect> extensionDialect = dialectByExtensionPoint.stream().filter((d) -> {
          return d.getResources().containsKey(url);
        }).findFirst();
        extensionDialect.ifPresent(dialect -> {
          scheduleUpdateDialect(event, dialect);
        });
      }

      @Override
      public void fileCreated(@NotNull VirtualFileEvent event) {
        updateDialectIfNeeded(event);
      }

      @Override
      public void fileDeleted(@NotNull VirtualFileEvent event) {
        updateDialectIfNeeded(event);
      }

      @Override
      public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        updateDialectIfNeeded(event);
      }

      @Override
      public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        updateDialectIfNeeded(event);
      }
    });

    //Registers open close projects
    EditorFactory.getInstance().addEditorFactoryListener(new LSPEditorListener(this), this);

    AlsLanguageServerFactory languageServerFactory = new AlsLanguageServerFactory(new ClientNotifier() {
      @Override
      public void notifyTelemetry(TelemetryMessage params) {

      }

      @Override
      public void notifyDiagnostic(PublishDiagnosticsParams params) {
        String uri = params.uri();
        DocumentState documentState = getDocumentState(uri);
        Collection<Diagnostic> diagnostics = asJavaCollection(params.diagnostics());
        documentState.clanDiagnostic();
        for (Diagnostic diagnostic : diagnostics) {
          documentState.addDiagnostic(diagnostic);
        }

        ReadAction.nonBlocking(() -> {
          VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(uri);
          if (fileByUrl != null) {
            PsiFile file = PsiManager.getInstance(myProject).findFile(fileByUrl);
            if (file != null) {
              DaemonCodeAnalyzer.getInstance(myProject).restart(file);
            }
          }
        });
      }
    });
    languageServerFactory.withSerializationProps(EmptyJvmSerializationProps$.MODULE$);
    languageServerFactory.withLogger(PrintLnLogger$.MODULE$);
    ClientResourceLoader clientResourceLoader = new ClientResourceLoader() {
      @Override
      public CompletableFuture<Content> fetch(String resource) {
        Optional<Content> first =
                dialectByExtensionPoint.stream()
                        .map((d) -> d.getResources().get(resource))
                        .filter((r) -> r != null)
                        .findFirst();
        if (first.isPresent()) {
          return CompletableFuture.completedFuture(first.get());
        } else {
          try {
            final URI uri = new URI(resource);
            final File file = new File(uri);
            //Use virtual file system cache here
            final VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(file);
            String content;
            if (fileByIoFile != null) {
              content = new String(fileByIoFile.contentsToByteArray(), StandardCharsets.UTF_8);
            } else {
              content = IOUtils.toString(uri, StandardCharsets.UTF_8);
            }
            return CompletableFuture.completedFuture(new Content(content, resource));
          } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
          }
          return CompletableFuture.failedFuture(new ResourceNotFound("Unable to find " + resource));
        }

      }
    };
    languageServerFactory.withResourceLoaders(Collections.singletonList(clientResourceLoader));
    languageServer = languageServerFactory.build();


    final CompletionClientCapabilities completionCapabilities = new CompletionClientCapabilities(Option.empty(), Option.empty(), Option.empty(), Option.empty());

    TextDocumentClientCapabilities textDocumentClientCapabilities = new TextDocumentClientCapabilities(
            Option.empty(),
            Option.apply(new DiagnosticClientCapabilities(Option.apply(true))),
            Option.apply(completionCapabilities),
            Option.apply(new ReferenceClientCapabilities(Option.apply(true))),
            Option.empty(),
            Option.apply(new DefinitionClientCapabilities(Option.apply(true), Option.apply(true))),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty()
    );

    WorkspaceClientCapabilities workspaceClientCapabilities = new WorkspaceClientCapabilities(
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.apply(new ExecuteCommandClientCapabilities(Option.apply(true)))
    );
    final AlsClientCapabilities clientCapabilities = new AlsClientCapabilities(
            Option.apply(workspaceClientCapabilities),
            Option.apply(textDocumentClientCapabilities),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty(),
            Option.empty()
    );

    final AlsInitializeParams params = AlsInitializeParams.apply(
            Option.apply(clientCapabilities),
            Option.apply(TraceKind.Off()),
            Option.apply(Locale.ENGLISH.toString()),
            Option.apply(getProjectRoot()),
            Option.empty(),
            Option.empty(),
            Option.apply(getProjectRoot()),
            Option.empty(),
            Option.empty(),
            Option.apply(true)
    );
    Future<AlsInitializeResult> initialize = languageServer.initialize(params);
    resultOf(initialize);
    languageServer.initialized();

    for (ALSLanguageExtension.Dialect supportedLanguage : dialectByExtensionPoint) {
      registerDialect(supportedLanguage);
    }
    registerUserDefinedDialects();
  }

  private void scheduleUpdateDialect(VirtualFileEvent event, ALSLanguageExtension.Dialect value) {
    myDocumentAlarm.cancelAllRequests();
    myDocumentAlarm.addRequest(() -> {
      if (myDocumentAlarm.isDisposed()) {
        return;
      }
      registerDialect(value);
      Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Updating dialect", "Dialect '" + event.getFileName() + "' was updated as changes where detected.", NotificationType.INFORMATION));
    }, 500);
  }

  public String getPath(String url) {
    String path = url;
    if (url.endsWith("/")) {
      path = url.substring(0, url.length() - 1);
    }
    return StringUtils.substringBeforeLast(path, "/");
  }

  public String getFile(String url) {
    String path = url;
    if (url.endsWith("/")) {
      path = url.substring(0, url.length() - 1);
    }
    return StringUtils.substringAfterLast(path, "/");
  }

  private void registerDialect(ALSLanguageExtension.Dialect supportedLanguage) {

    final String dialect = "{" + "\"uri\": \"" + supportedLanguage.getDialectUrl() + "\"" + "} ";
    final ExecuteCommandParams executeCommandParams = new ExecuteCommandParams(
            Commands.INDEX_DIALECT(), JavaConverters.asScalaBuffer(Collections.singletonList(dialect)).toList());
    try {
      Future<Object> objectFuture = languageServer.workspaceService().executeCommand(executeCommandParams);
      resultOf(objectFuture);
    } catch (Exception e) {
      Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Unable to register dialect", "Unable to register dialect `" + supportedLanguage.getDialectUrl() + "`\nReason:\n" + e.getMessage(), NotificationType.ERROR));
    }
  }

  private @NonNls String getProjectRoot() {
    return LSPUtils.toLSPUrl(myProject.getPresentableUrl());
  }

  public void registerUserDefinedDialects() {
    List<DialectsRegistry.DialectLocation> dialectLocations = DialectsRegistry.getInstance().getDialectsRegistry();
    for (DialectsRegistry.DialectLocation dialectLocation : dialectLocations) {
      UserDialectLanguageExtension userDialectLanguageExtension = new UserDialectLanguageExtension(dialectLocation.getName(), dialectLocation.getDialectFilePath());
      Optional<ALSLanguageExtension.Dialect> dialect = userDialectLanguageExtension.customDialect(myProject);
      if (dialect.isPresent()) {
        String dialectUrl = dialect.get().getDialectUrl();
        try {
          if (new File(new URI(dialectUrl)).exists() && !dialectUrl.isBlank()) {
            Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, "Registering dialect", "Dialect '" + dialectLocation.getName() + "'.", NotificationType.INFORMATION));
            registerDialect(dialect.get());
            //If file doesn't exists don't register it
          }
        } catch (Exception e) {
          //
        }
      }
    }
  }

  private DocumentState getDocumentState(String uri) {
    DocumentState documentState = documents.get(uri);
    if (documentState == null) {
      DocumentState newState = new DocumentState(uri);
      documents.put(uri, newState);
      return newState;
    } else {
      return documentState;
    }
  }


  public void openEditor(PsiFile file) {
    final String url = getUrl(file);
    if (url == null) {
      return;
    }
    final String text = file.getText();
    final DocumentState documentState = getDocumentState(url);
    final DidOpenTextDocumentParams didOpenTextDocumentParams = new DidOpenTextDocumentParams(new TextDocumentItem(url, file.getLanguage().getID(), documentState.version(), text));
    languageServer.textDocumentSyncConsumer().didOpen(didOpenTextDocumentParams);
  }

  public void onChanged(PsiFile file) {
    final String url = getUrl(file);
    if (url == null) {
      return;
    }

    DidChangeTextDocumentParams compute = ReadAction.compute(() -> {
      final String text = file.getText();
      DocumentState documentState = getDocumentState(url);
      TextDocumentContentChangeEvent textDocumentContentChangeEvent = new TextDocumentContentChangeEvent(file.getText(), Option.empty(), Option.empty());
      return new DidChangeTextDocumentParams(new VersionedTextDocumentIdentifier(url, Option.apply(documentState.changed())), ScalaUtils.toSeq(textDocumentContentChangeEvent));
    });
    AppExecutorUtil.getAppExecutorService().execute(() -> {
      languageServer.textDocumentSyncConsumer().didChange(compute);
    });
  }

  public void onClose(PsiFile file) {
    final String url = getUrl(file);
    if (url == null) {
      return;
    }
    final String text = file.getText();
    final DocumentState documentState = getDocumentState(url);
    final DidCloseTextDocumentParams didOpenTextDocumentParams = new DidCloseTextDocumentParams(documentIdentifierOf(file));
    languageServer.textDocumentSyncConsumer().didClose(didOpenTextDocumentParams);
  }

  @NotNull
  public List<LookupElement> completion(@NotNull PsiElement position) {
    final PsiFile file = position.getContainingFile().getOriginalFile();
    if (file.getVirtualFile() == null) {
      return Collections.emptyList();
    }
    ensureFileOpened(file);
    final RequestHandler<CompletionParams, Either<Seq<CompletionItem>, CompletionList>> completionParamsEitherRequestHandler = languageServer.resolveHandler(CompletionRequestType$.MODULE$).get();
    final Future<Either<Seq<CompletionItem>, CompletionList>> apply = completionParamsEitherRequestHandler.apply(new CompletionParams(documentIdentifierOf(file), positionOf(position), Option.empty()));
    Optional<Either<Seq<CompletionItem>, CompletionList>> eitherOptional = resultOf(apply);
    if (eitherOptional.isEmpty()) {
      return Collections.emptyList();
    }
    final Either<Seq<CompletionItem>, CompletionList> result = eitherOptional.get();
    Seq<CompletionItem> completionList;
    if (result.isLeft()) {
      completionList = result.left().get();
    } else {
      completionList = result.right().get().items();
    }
    final Collection<CompletionItem> completionItems = asJavaCollection(completionList);
    return completionItems.stream().map((ci) -> {
      final Option<Enumeration.Value> valueOption = ci.insertTextFormat();
      final boolean isSnippet = valueOption.isDefined() && valueOption.get().id() == InsertTextFormat.Snippet().id();
      final Option<Enumeration.Value> kind = ci.kind();
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(ci.label());
      if (kind.isDefined()) {
        lookupElementBuilder = lookupElementBuilder.withIcon(getCompletionIcon(kind.get().id()));
      } else if (isSnippet) {
        lookupElementBuilder = lookupElementBuilder.withIcon(AllIcons.Nodes.AbstractMethod);
      } else {
        lookupElementBuilder = lookupElementBuilder.withIcon(AllIcons.Nodes.Property);
      }
      if (isSnippet) {
        String insertText = toOptional(ci.insertText()).orElse(toOptional(ci.filterText()).orElse(ci.label()));
        final Matcher matcher = VARIABLES.matcher(insertText);
        final ArrayList<String> variables = new ArrayList<>();
        insertText = matcher.replaceAll((mr) -> {
          String newName = "var" + mr.group(1);
          variables.add(newName);
          return "\\$" + newName + "\\$";
        });
        final Template myTemplate = TemplateManager.getInstance(file.getProject()).createTemplate("template", "als_suggest", insertText);
        for (String variable : variables) {
          myTemplate.addVariable(variable, "complete()", "", true);
        }
        lookupElementBuilder = lookupElementBuilder.withInsertHandler((context, item1) -> {
          final int selectionStart = context.getEditor().getCaretModel().getOffset();
          final int startOffset = context.getStartOffset();
          final int tailOffset = context.getTailOffset();
          context.setAddCompletionChar(false);
          context.getDocument().deleteString(startOffset, tailOffset);
          TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
        });
      } else {
        if (ci.insertText().isDefined()) {
          lookupElementBuilder = lookupElementBuilder.withInsertHandler((context, item1) -> {
            final int selectionStart = context.getEditor().getCaretModel().getOffset();
            final int startOffset = context.getStartOffset();
            final int tailOffset = context.getTailOffset();
            context.setAddCompletionChar(false);
            final Template myTemplate = TemplateManager.getInstance(file.getProject()).createTemplate("template", "als_suggest", ci.insertText().get());
            TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
          });
        } else if (ci.textEdit().isDefined()) {
          Either<TextEdit, InsertReplaceEdit> edits = ci.textEdit().get();
          if (edits.isLeft()) {
            TextEdit textEdit = edits.left().get();
            lookupElementBuilder = lookupElementBuilder.withInsertHandler((context, item1) -> {
              final int selectionStart = context.getEditor().getCaretModel().getOffset();
              final int startOffset = context.getStartOffset();
              final int tailOffset = context.getTailOffset();
              context.setAddCompletionChar(false);
              context.getDocument().deleteString(startOffset, tailOffset);
              final Template myTemplate = TemplateManager.getInstance(file.getProject()).createTemplate("template", "als_suggest", textEdit.newText());
              TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
            });
          }
        }
      }
      if (ci.documentation().isDefined()) {
        lookupElementBuilder = lookupElementBuilder.withTypeText(ci.documentation().get());
      } else if (ci.detail().isDefined()) {
        lookupElementBuilder = lookupElementBuilder.withTypeText(ci.detail().get());
      } else {
        lookupElementBuilder = lookupElementBuilder.withTypeText("field");
      }
      if (ci.deprecated().isDefined() && Boolean.TRUE.equals(ci.deprecated().get())) {
        lookupElementBuilder = lookupElementBuilder.withStrikeoutness(true);
      }

      return lookupElementBuilder;
    }).collect(Collectors.toList());


  }

  public Icon getCompletionIcon(int kind) {
    if (CompletionItemKind.Class().id() == kind) {
      return AllIcons.Nodes.Class;
    } else if (CompletionItemKind.Enum().id() == kind) {
      return AllIcons.Nodes.Enum;
    } else if (CompletionItemKind.Field().id() == kind) {
      return AllIcons.Nodes.Field;
    } else if (CompletionItemKind.File().id() == kind) {
      return AllIcons.FileTypes.Any_type;
    } else if (CompletionItemKind.Function().id() == kind) {
      return AllIcons.Nodes.Function;
    } else if (CompletionItemKind.Interface().id() == kind) {
      return AllIcons.Nodes.Interface;
    } else if (CompletionItemKind.Keyword().id() == kind) {
      return AllIcons.Nodes.UpLevel;
    } else if (CompletionItemKind.Method().id() == kind) {
      return AllIcons.Nodes.Method;
    } else if (CompletionItemKind.Module().id() == kind) {
      return AllIcons.Nodes.Module;
    } else if (CompletionItemKind.Property().id() == kind) {
      return AllIcons.Nodes.Property;
    } else if (CompletionItemKind.Reference().id() == kind) {
      return AllIcons.Nodes.MethodReference;
    } else if (CompletionItemKind.Snippet().id() == kind) {
      return AllIcons.Nodes.Static;
    } else if (CompletionItemKind.Text().id() == kind) {
      return AllIcons.FileTypes.Text;
    } else if (CompletionItemKind.Unit().id() == kind) {
      return AllIcons.Nodes.Artifact;
    } else if (CompletionItemKind.Variable().id() == kind) {
      return AllIcons.Nodes.Variable;
    } else {
      return AllIcons.Nodes.Field;
    }
  }

  private void ensureFileOpened(PsiFile file) {
    if (!wasFileOpened(file)) {
      openEditor(file);
    } else {
      onChanged(file);
    }
  }

  @Nullable
  public String hover(PsiElement element) {
    String result = null;
    ensureFileOpened(element.getContainingFile());
    Option<RequestHandler<HoverParams, Hover>> requestHandlerOption = languageServer.resolveHandler(HoverRequestType$.MODULE$);
    if (requestHandlerOption.isDefined()) {
      Future<Hover> apply = requestHandlerOption.get().apply(new HoverParams(documentIdentifierOf(element.getContainingFile()), positionOf(element)));
      final Optional<Hover> optionalHover = resultOf(apply);
      if (optionalHover.isPresent()) {
        final Hover hover = optionalHover.get();
        @NotNull String[] strings = ScalaUtils.toArray(hover.contents(), new String[0]);
        result = String.join("\n", strings);
      }
    }
    return result;
  }

  public boolean isSupportedFile(PsiFile file) {
    if (file == null) {
      return false;
    }
    List<DialectsRegistry.DialectLocation> dialectLocations = DialectsRegistry.getInstance().getDialectsRegistry();
    for (DialectsRegistry.DialectLocation dialectLocation : dialectLocations) {
      UserDialectLanguageExtension userDialectLanguageExtension = new UserDialectLanguageExtension(dialectLocation.getName(), dialectLocation.getDialectFilePath());
      if (userDialectLanguageExtension.supports(file)) {
        return true;
      }
    }
    return Arrays.stream(supportedLanguages).anyMatch((l) -> l.supports(file));
  }

  private boolean wasFileOpened(PsiFile file) {
    final VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
    if (virtualFile != null) {
      return documents.containsKey(virtualFile.getUrl());
    } else {
      return false;
    }
  }

  @Override
  public void dispose() {

  }

  public void closeEditor(Editor editor) {
    VirtualFile file = LSPUtils.virtualFileFromEditor(editor);
    String url = file.getUrl();
    if (documents.containsKey(url)) {
      documents.remove(url);
      languageServer.textDocumentSyncConsumer().didClose(new DidCloseTextDocumentParams(new TextDocumentIdentifier(url)));
    }
  }

  public void openEditor(Editor editor) {
    final VirtualFile file = LSPUtils.virtualFileFromEditor(editor);
    final String url = file.getUrl();
    final Project project = editor.getProject();
    if (project != null) {
      final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
      if (psiFile != null && isSupportedFile(psiFile)) {
        openEditor(psiFile);
      }
    }
  }

  public List<Diagnostic> diagnosticsOf(PsiFile collectedInfo) {
    final String url = getUrl(collectedInfo);
    final List<Diagnostic> result = new ArrayList<>();
    if (url != null) {
      ensureFileOpened(collectedInfo);
      final DocumentState documentState = documents.get(url);
      if (documentState != null) {
        result.addAll(documentState.diagnostics());
      }
    }
    return result;
  }
}
