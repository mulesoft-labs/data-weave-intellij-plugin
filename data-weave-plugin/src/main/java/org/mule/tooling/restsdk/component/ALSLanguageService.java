package org.mule.tooling.restsdk.component;

import amf.core.client.common.remote.Content;
import amf.core.client.platform.resource.ClientResourceLoader;
import amf.core.client.platform.resource.ResourceNotFound;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.quickfix.EmptyExpression;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mulesoft.als.configuration.AlsConfiguration;
import org.mulesoft.als.configuration.ConfigurationStyle;
import org.mulesoft.als.configuration.ProjectConfigurationStyle;
import org.mulesoft.als.logger.PrintLnLogger$;
import org.mulesoft.als.server.EmptyJvmSerializationProps$;
import org.mulesoft.als.server.client.ClientNotifier;
import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeClientCapabilities;
import org.mulesoft.als.server.feature.fileusage.FileUsageClientCapabilities;
import org.mulesoft.als.server.feature.renamefile.RenameFileActionClientCapabilities;
import org.mulesoft.als.server.feature.serialization.ConversionClientCapabilities;
import org.mulesoft.als.server.feature.serialization.SerializationClientCapabilities;
import org.mulesoft.als.server.lsp4j.LanguageServerFactory;
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
import org.mulesoft.lsp.feature.common.*;
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
import org.mulesoft.lsp.feature.hover.HoverClientCapabilities;
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
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.util.Either;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.mule.tooling.lang.dw.util.ScalaUtils.toOptional;
import static scala.collection.JavaConverters.asJavaCollection;

public class ALSLanguageService implements Disposable {
  public static final String REST_SDK_DIALECT_YAML_PATH = "com/mulesoft/connectivity/rest/aml/dialects/rest_sdk_dialect.yaml";
  public static final String REST_SDK_DIALECT_URL = "file:///" + REST_SDK_DIALECT_YAML_PATH;

  public static final String REST_SDK_VOCABULARY_YAML_PATH = "com/mulesoft/connectivity/rest/aml/vocabularies/rest_sdk_vocabulary.yaml";
  public static final String REST_SDK_VOCABULARY_URL = "file:///" + REST_SDK_VOCABULARY_YAML_PATH;


  static Pattern VARIABLES = Pattern.compile("\\$([0-9]+)");


  private final Project myProject;
  private LanguageServer languageServer;
  private Map<String, DocumentState> documents = new HashMap<String, DocumentState>();

  public ALSLanguageService(Project project) {
    myProject = project;
    init();
  }

  public static ALSLanguageService getInstance(Project project) {
    return ServiceManager.getService(project, ALSLanguageService.class);
  }


  public void init() {
    PsiManager.getInstance(this.myProject).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
      @Override
      protected void onChange(@Nullable PsiFile file) {
        if (file != null && wasFileOpened(file)) {
          onChanged(file);
        }
      }
    }, this);

    EditorFactory.getInstance().addEditorFactoryListener(new LSPEditorListener(this), this);

    LanguageServerFactory languageServerFactory = new LanguageServerFactory(new ClientNotifier() {
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

        ReadAction.run(() -> {
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
        try {
          String path = null;
          if (resource.equals(REST_SDK_DIALECT_URL)) {
            path = REST_SDK_DIALECT_YAML_PATH;
          } else if (resource.equals(REST_SDK_VOCABULARY_URL)) {
            path = REST_SDK_VOCABULARY_YAML_PATH;
          }
          if (path != null) {
            URL resourceUrl = getClass().getClassLoader().getResource(path);
            if (resourceUrl != null) {
              String content = IOUtils.toString(resourceUrl, StandardCharsets.UTF_8);
              return CompletableFuture.completedFuture(new Content(content, resourceUrl.toExternalForm(), "application/yaml"));
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return CompletableFuture.failedFuture(new ResourceNotFound("Unable to find " + resource));

      }
    };
    languageServerFactory.withResourceLoaders(Collections.singletonList(clientResourceLoader));
    languageServer = languageServerFactory.build();


    final CompletionClientCapabilities completionCapabilities = new CompletionClientCapabilities(Option.empty(), Option.<CompletionItemClientCapabilities>empty(), Option.<CompletionItemKindClientCapabilities>empty(), Option.empty());

    TextDocumentClientCapabilities textDocumentClientCapabilities = new TextDocumentClientCapabilities(
            Option.<SynchronizationClientCapabilities>empty(),
            Option.apply(new DiagnosticClientCapabilities(Option.apply(true))),
            Option.apply(completionCapabilities),
            Option.apply(new ReferenceClientCapabilities(Option.apply(true))),
            Option.<DocumentSymbolClientCapabilities>empty(),
            Option.apply(new DefinitionClientCapabilities(Option.apply(true), Option.apply(true))),
            Option.<ImplementationClientCapabilities>empty(),
            Option.<TypeDefinitionClientCapabilities>empty(),
            Option.<RenameClientCapabilities>empty(),
            Option.<CodeActionCapabilities>empty(),
            Option.<DocumentLinkClientCapabilities>empty(),
            Option.<HoverClientCapabilities>empty(),
            Option.<DocumentHighlightCapabilities>empty(),
            Option.<FoldingRangeCapabilities>empty(),
            Option.<SelectionRangeCapabilities>empty(),
            Option.<DocumentFormattingClientCapabilities>empty(),
            Option.<DocumentRangeFormattingClientCapabilities>empty()
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
            Option.<SerializationClientCapabilities>empty(),
            Option.<CleanDiagnosticTreeClientCapabilities>empty(),
            Option.<FileUsageClientCapabilities>empty(),
            Option.<ConversionClientCapabilities>empty(),
            Option.<RenameFileActionClientCapabilities>empty(),
            Option.empty(),
            Option.empty()
    );

    final AlsInitializeParams params = AlsInitializeParams.apply(
            Option.apply(clientCapabilities),
            Option.apply(TraceKind.Off()),
            Option.apply(Locale.ENGLISH.toString()),
            Option.apply(myProject.getPresentableUrl()),
            Option.empty(),
            Option.<Seq<WorkspaceFolder>>empty(),
            Option.apply(myProject.getPresentableUrl()),
            Option.empty(),
            Option.<AlsConfiguration>empty(),
            Option.apply(new ProjectConfigurationStyle(ConfigurationStyle.COMMAND()))

    );
    Future<AlsInitializeResult> initialize = languageServer.initialize(params);
    try {
      Await.result(initialize, Duration.Inf());
    } catch (InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    languageServer.initialized();
    final String dependencies = "{\"mainUri\": \"\", \"dependencies\": [{\"file\": \"" + REST_SDK_DIALECT_URL + "\", \"scope\": \"" + KnownDependencyScopes.SEMANTIC_EXTENSION() + "\"}]} ";
    final ExecuteCommandParams executeCommandParams = new ExecuteCommandParams(
            Commands.DID_CHANGE_CONFIGURATION(), JavaConverters.asScalaBuffer(Collections.singletonList(dependencies)).toList());
    Future<Object> objectFuture = languageServer.workspaceService().executeCommand(executeCommandParams);
    try {
      Await.result(objectFuture, Duration.Inf());
    } catch (InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
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
    String url = file.getVirtualFile().getUrl();
    String text = file.getText();
    DocumentState documentState = getDocumentState(url);
    DidOpenTextDocumentParams didOpenTextDocumentParams = new DidOpenTextDocumentParams(new TextDocumentItem(url, file.getLanguage().getID(), documentState.version(), text));
    languageServer.textDocumentSyncConsumer().didOpen(didOpenTextDocumentParams);
  }

  public void onChanged(PsiFile file) {
    String url = file.getVirtualFile().getUrl();
    String text = file.getText();
    DocumentState documentState = getDocumentState(url);
    TextDocumentContentChangeEvent textDocumentContentChangeEvent = new TextDocumentContentChangeEvent(file.getText(), Option.<Range>empty(), Option.empty());

    DidChangeTextDocumentParams didOpenTextDocumentParams = new DidChangeTextDocumentParams(new VersionedTextDocumentIdentifier(url, Option.apply(documentState.changed())), ScalaUtils.toSeq(textDocumentContentChangeEvent));
    languageServer.textDocumentSyncConsumer().didChange(didOpenTextDocumentParams);
  }

  public void onClose(PsiFile file) {
    String url = file.getVirtualFile().getUrl();
    String text = file.getText();
    DocumentState documentState = getDocumentState(url);
    DidCloseTextDocumentParams didOpenTextDocumentParams = new DidCloseTextDocumentParams(new TextDocumentIdentifier(url));
    languageServer.textDocumentSyncConsumer().didClose(didOpenTextDocumentParams);
  }

  public List<LookupElement> completion(PsiFile file, int line, int column) {
    ensureFileOpened(file);
    String url = file.getVirtualFile().getUrl();
    String text = file.getText();
    final RequestHandler<CompletionParams, Either<Seq<CompletionItem>, CompletionList>> completionParamsEitherRequestHandler = languageServer.resolveHandler(CompletionRequestType$.MODULE$).get();
    final Future<Either<Seq<CompletionItem>, CompletionList>> apply = completionParamsEitherRequestHandler.apply(new CompletionParams(new TextDocumentIdentifier(url), new Position(line, column), Option.empty()));
    try {
      final Either<Seq<CompletionItem>, CompletionList> result = Await.result(apply, Duration.Inf());
      Seq<CompletionItem> completionList;
      if (result.isLeft()) {
        completionList = result.left().get();
      } else {
        completionList = result.right().get().items();
      }
      final Collection<CompletionItem> completionItems = asJavaCollection(completionList);
      return completionItems.stream().map((ci) -> {

        Option<Enumeration.Value> valueOption = ci.insertTextFormat();
        boolean isSnippet = valueOption.isDefined() && valueOption.get().id() == InsertTextFormat.Snippet().id();
        Option<Enumeration.Value> kind = ci.kind();
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
            myTemplate.addVariable(variable, new EmptyExpression(), true);
          }
          lookupElementBuilder = lookupElementBuilder.withInsertHandler((context, item1) -> {
            final int selectionStart = context.getEditor().getCaretModel().getOffset();
            final int startOffset = context.getStartOffset();
            final int tailOffset = context.getTailOffset();
            context.getDocument().deleteString(startOffset, tailOffset);
            context.setAddCompletionChar(false);
            TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
          });
        } else {
          if (ci.insertText().isDefined()) {
            lookupElementBuilder = lookupElementBuilder.withInsertHandler((context, item1) -> {
              final int selectionStart = context.getEditor().getCaretModel().getOffset();
              final int startOffset = context.getStartOffset();
              final int tailOffset = context.getTailOffset();
              context.setAddCompletionChar(false);
              context.getDocument().replaceString(startOffset, tailOffset, ci.insertText().get());
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
                context.getDocument().replaceString(startOffset, tailOffset, textEdit.newText());
              });
            }
          }
        }
        if (ci.detail().isDefined()) {
          lookupElementBuilder = lookupElementBuilder.withTypeText(ci.detail().get());
        } else if (ci.documentation().isDefined()) {
          lookupElementBuilder = lookupElementBuilder.withTypeText(ci.documentation().get());
        }

        if (ci.filterText().isDefined()) {
          lookupElementBuilder = lookupElementBuilder.withTypeText(ci.filterText().get());
        }
        if (ci.deprecated().isDefined() && Boolean.TRUE.equals(ci.deprecated().get())) {
          lookupElementBuilder = lookupElementBuilder.withStrikeoutness(true);
        }

        return lookupElementBuilder;
      }).collect(Collectors.toList());

    } catch (InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
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

  public boolean isSupportedFile(PsiFile file) {
    //TODO make this extensible
    return RestSdkHelper.isRestSdkDescriptorFile(file);
  }

  private boolean wasFileOpened(PsiFile file) {
    return documents.containsKey(file.getVirtualFile().getUrl());
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
    final PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
    if (psiFile != null && isSupportedFile(psiFile)) {
      openEditor(psiFile);
    }
  }

  public List<Diagnostic> diagnosticsOf(PsiFile collectedInfo) {
    ensureFileOpened(collectedInfo);
    final List<Diagnostic> result = new ArrayList<>();
    final DocumentState documentState = documents.get(collectedInfo.getVirtualFile().getUrl());
    if (documentState != null) {
      result.addAll(documentState.diagnostics());
    }
    return result;
  }
}
