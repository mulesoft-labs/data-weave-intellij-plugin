package org.mule.tooling.restsdk.completion;


import amf.client.model.domain.*;
import amf.core.model.DataType;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.YamlPath;
import webapi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mule.tooling.restsdk.datasense.RestSdkInputOutputTypesProvider.OPERATION_PATH;
import static org.mule.tooling.restsdk.utils.MapUtils.map;

public class RestSdkCompletionService {


  public static YamlPath api = YamlPath.DOCUMENT.child("apiSpec").child("url");
  public static YamlPath swaggerVersion = YamlPath.DOCUMENT.child("swagger");
  public static YamlPath openApiVersion = YamlPath.DOCUMENT.child("openapi");

  public static YamlPath baseOperation = OPERATION_PATH.any().child("base");
  public static YamlPath queryParameters = OPERATION_PATH.any().child("request").child("queryParameter");
  public static YamlPath headers = OPERATION_PATH.any().child("request").child("header");
  public static YamlPath uriParameters = OPERATION_PATH.any().child("request").child("uriParameter");

  public static YamlPath relativeBasePath = YamlPath.PARENT.parent().child("base");


  Map<String, String> SIMPLE_TYPE_MAP = map(
          DataType.String(), "string",
          DataType.Boolean(), "boolean",
          DataType.Number(), "number",
          DataType.Decimal(), "number",
          DataType.Integer(), "integer",
          DataType.Float(), "number",
          DataType.Long(), "long",
          DataType.DateTime(), "zonedDateTime",
          DataType.Time(), "time",
          DataType.DateTimeOnly(), "localDateTime",
          DataType.Date(), "date",
          DataType.AnyUri(), "string",
          DataType.Duration(), "duration",
          DataType.Nil(), "null",
          DataType.File(), "file",
          DataType.Byte(), "file",
          DataType.Any(), "any",
          DataType.Password(), "string",
          DataType.Link(), "string");

  public List<LookupElement> completions(CompletionParameters completionParameters) {
    final Project project = completionParameters.getOriginalFile().getProject();
    final ArrayList<LookupElement> result = new ArrayList<>();

    final PsiElement position = completionParameters.getPosition();
    final PsiElement parentElement = position.getParent();
    final YamlPath yamlPath = YamlPath.pathOf(parentElement);
    if (yamlPath.matches(baseOperation) ||
            yamlPath.matches(OPERATION_PATH) ||
            yamlPath.matches(queryParameters) ||
            yamlPath.matches(headers) ||
            yamlPath.matches(uriParameters)) {
      suggestBaseOperations(completionParameters, project, result, yamlPath);
    }
    return result;
  }

  private void suggestBaseOperations(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, YamlPath yamlPath) {
    final PsiElement select = api.select(completionParameters.getOriginalFile());
    if (select instanceof YAMLScalar) {
      String apiPath = ((YAMLScalar) select).getTextValue();
      //
      final VirtualFile parent = completionParameters.getOriginalFile().getVirtualFile().getParent();
      final VirtualFile child = parent.findFileByRelativePath(apiPath);
      if (child != null) {
        try {
          final WebApi webApi = parseWebApi(project, child);
          if (webApi != null) {
            if (yamlPath.matches(baseOperation)) {
              suggestOperationBase(result, webApi);
            } else if (yamlPath.matches(OPERATION_PATH)) {
              suggestOperationTemplate(project, result, webApi);
            } else {
              PsiElement base = relativeBasePath.select(completionParameters.getPosition());
              if (base instanceof YAMLScalar) {
                String baseOperation = ((YAMLScalar) base).getTextValue();
                Optional<Operation> maybeOperation = webApi.endPoints().stream().flatMap((endpoint) -> {
                  return endpoint.operations().stream().filter((operation) -> operation.name().value().equals(baseOperation));
                }).findFirst();
                if (maybeOperation.isPresent()) {
                  if (yamlPath.matches(queryParameters)) {
                    List<Parameter> queryParameters = maybeOperation.get().request().queryParameters();
                    queryParameters.forEach((queryParam) -> {
                      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
                      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
                      result.add(elementBuilder);
                    });
                  } else if (yamlPath.matches(headers)) {
                    List<Parameter> queryParameters = maybeOperation.get().request().headers();
                    queryParameters.forEach((queryParam) -> {
                      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
                      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
                      result.add(elementBuilder);
                    });
                  } else if (yamlPath.matches(uriParameters)) {
                    List<Parameter> queryParameters = maybeOperation.get().request().uriParameters();
                    queryParameters.forEach((queryParam) -> {
                      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
                      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
                      result.add(elementBuilder);
                    });
                  }
                }
              }

            }
          }
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Unable to resolver path : `" + apiPath + "`");
      }
    }
  }

  private void suggestOperationTemplate(Project project, ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + "(New Operation)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation), true);
        String template =
                "$name$:\n" +
                        "  displayName: $name$\n" +
                        "  base: " + operation.name() + "\n" +
                        "  parameters: ";

        Request request = operation.request();
        if (request != null) {
          final List<Payload> payloads = request.payloads();
          final List<Parameter> headers = request.headers();
          final List<Parameter> uriParameters = request.uriParameters();
          final List<Parameter> queryParameters = request.queryParameters();
          if (!payloads.isEmpty()) {
            Shape schema = payloads.get(0).schema();
            if (schema instanceof NodeShape) {
              List<PropertyShape> properties = ((NodeShape) schema).properties();
              for (PropertyShape property : properties) {
                template = template + "\n" + "    " + property.name().value() + " : ";
                template = template + "\n" + "      " + "type" + ": " + toSchemaName(property.range());
                template = template + "\n" + "      " + "displayName" + ": " + property.name().value();
                template = template + "\n" + "      " + "required" + ": " + (property.minCount().value() == 0);
                if (!property.description().isNullOrEmpty()) {
                  template = template + "\n" + "      " + "description" + ": " + property.description().value();
                }
              }
            }
          }
          template = template + buildParms(headers);
          template = template + buildParms(uriParameters);
          template = template + buildParms(queryParameters);

          template = template + "\n  " + "request: ";
          template = template + "\n    " + "body: ";
          template = template + "\n      " + "expression: ";

          if (!headers.isEmpty()) {
            template = template + "\n    " + "header: ";
            for (Parameter header : headers) {
              template = template + "\n      " + header.name().value() + ": #[]";
            }
          }

          if (!queryParameters.isEmpty()) {
            template = template + "\n    " + "queryParameters: ";
            for (Parameter queryParam : queryParameters) {
              template = template + "\n      " + queryParam.name().value() + ": #[]";
            }
          }
        }
        template = template + "\n  " + "response: ";
        template = template + "\n    " + "body: \"#[payload]\"";


        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "rest_sdk_suggest", template);
        myTemplate.addVariable("name", new TextExpression(operation.name().value()), true);
        elementBuilder = elementBuilder.withInsertHandler((context, item1) -> {
          final int selectionStart = context.getEditor().getCaretModel().getOffset();
          final int startOffset = context.getStartOffset();
          final int tailOffset = context.getTailOffset();
          context.getDocument().deleteString(startOffset, tailOffset);
          context.setAddCompletionChar(false);
          TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
        });

        result.add(elementBuilder);
      });
    });
  }

  @NotNull
  private String buildParms(List<Parameter> queryParameters) {
    String queryParams = "";
    for (Parameter queryParam : queryParameters) {
      Shape schema = queryParam.schema();
      queryParams = queryParams + "\n" + "    " + queryParam.name().value() + " : ";
      queryParams = queryParams + "\n" + "      " + "type" + ": " + toSchemaName(schema);
      queryParams = queryParams + "\n" + "      " + "displayName" + ": " + queryParam.name().value();
      queryParams = queryParams + "\n" + "      " + "required" + ": " + (queryParam.required());
      if (!queryParam.description().isNullOrEmpty()) {
        queryParams = queryParams + "\n" + "      " + "description" + ": " + "\"" + queryParam.description().value() + "\"";
      }
    }
    return queryParams;
  }

  private String toSchemaName(Shape schema) {
    if (schema instanceof ScalarShape) {
      return SIMPLE_TYPE_MAP.get(((ScalarShape) schema).dataType().value());
    } else {
      //TODO
      return "";
    }

  }

  private void suggestOperationBase(ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name());
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation), true);
        result.add(elementBuilder);
      });
    });
  }

  private String operationType(Operation operation) {
    String result = operation.method().value() + " (";
    Request request = operation.request();
    if (request != null) {
      List<Payload> payloadRequest = request.payloads();
      if (!payloadRequest.isEmpty()) {
        final Payload payload = payloadRequest.get(0);
        result = result + payload.name().value() + ":" + payload.schema().description();
      }
    }
    result = result + ")";
    List<Response> responses = operation.responses();
    if (!responses.isEmpty()) {
      List<Payload> payloads = responses.get(0).payloads();
      if (!payloads.isEmpty()) {
        result = result + " -> " + payloads.get(0).schema().description();
      }
    }

    return result;
  }

  private @Nullable WebApi parseWebApi(Project project, VirtualFile child) throws InterruptedException, ExecutionException {
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(child);
    if (psiFile != null) {
      CompletableFuture<WebApiBaseUnit> parse;
      if (psiFile.getFileType() instanceof YAMLFileType) {
        if (swaggerVersion.select(psiFile) != null) {
          parse = Oas20.parseYaml(child.getUrl());
        } else if (openApiVersion.select(psiFile) != null) {
          parse = Oas30.parseYaml(child.getUrl());
        } else {
          parse = Raml10.parse(child.getUrl());
        }
      } else if (psiFile.getFileType() instanceof JsonFileType) {
        //TODO try to detect version better
        parse = Oas30.parse(child.getUrl());
      } else {
        return null;
      }
      final WebApiDocument webApiBaseUnit = (WebApiDocument) parse.get();
      return (WebApi) webApiBaseUnit.encodes();
    } else {
      return null;
    }
  }
}
