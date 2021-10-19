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
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.YamlPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mule.tooling.restsdk.utils.MapUtils.map;
import static org.mule.tooling.restsdk.utils.RestSdkHelper.parseWebApi;
import static org.mule.tooling.restsdk.utils.RestSdkPaths.OPERATION_PATH;

public class RestSdkCompletionService {


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
    if (yamlPath.matches(RestSdkPaths.OPERATION_BASE_PATH) ||
            yamlPath.matches(OPERATION_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_QUERY_PARAMS_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_URI_PARAMS_PATH)) {
      suggestBaseOperations(completionParameters, project, result, yamlPath);
    }
    return result;
  }

  private void suggestBaseOperations(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, YamlPath yamlPath) {
    final WebApi webApi = parseWebApi(completionParameters.getOriginalFile());
    if (webApi != null) {
      if (yamlPath.matches(RestSdkPaths.OPERATION_BASE_PATH)) {
        suggestOperationBase(result, webApi);
      } else if (yamlPath.matches(OPERATION_PATH)) {
        suggestOperationTemplate(project, result, webApi);
      } else {
        PsiElement base = RestSdkPaths.RELATIVE_BASE_PATH.select(completionParameters.getPosition());
        if (base instanceof YAMLScalar) {
          String baseOperation = ((YAMLScalar) base).getTextValue();
          Optional<Operation> maybeOperation = webApi.endPoints().stream().flatMap((endpoint) -> {
            return endpoint.operations().stream().filter((operation) -> operation.name().value().equals(baseOperation));
          }).findFirst();
          if (maybeOperation.isPresent()) {
            if (yamlPath.matches(RestSdkPaths.OPERATION_QUERY_PARAMS_PATH)) {
              List<Parameter> queryParameters = maybeOperation.get().request().queryParameters();
              queryParameters.forEach((queryParam) -> {
                LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
                elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
                result.add(elementBuilder);
              });
            } else if (yamlPath.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_PATH)) {
              List<Parameter> queryParameters = maybeOperation.get().request().headers();
              queryParameters.forEach((queryParam) -> {
                LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
                elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
                result.add(elementBuilder);
              });
            } else if (yamlPath.matches(RestSdkPaths.OPERATION_URI_PARAMS_PATH)) {
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
  }

  private void suggestOperationTemplate(Project project, ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + " (Scaffold New Operation)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation), true);
        final StringBuilder template =
                new StringBuilder("$name$:\n" +
                        "  displayName: $name$\n" +
                        "  base: " + operation.name() + "\n" +
                        "  parameters: ");

        final Request request = operation.request();
        if (request != null) {
          final List<Payload> payloads = request.payloads();
          final List<Parameter> headers = request.headers();
          final List<Parameter> uriParameters = request.uriParameters();
          final List<Parameter> queryParameters = request.queryParameters();
          if (!payloads.isEmpty()) {
            final Shape schema = payloads.get(0).schema();
            if (schema instanceof NodeShape) {
              List<PropertyShape> properties = ((NodeShape) schema).properties();
              for (PropertyShape property : properties) {
                template.append("\n").append("    ").append(property.name().value()).append(": ");
                template.append("\n").append("      ").append("type").append(": ").append(toSchemaName(property.range()));
                template.append("\n").append("      ").append("displayName").append(": ").append(property.name().value());
                template.append("\n").append("      ").append("required").append(": ").append(property.minCount().value() == 0);
                if (!property.description().isNullOrEmpty()) {
                  template.append("\n").append("      ").append("description").append(": ").append(property.description().value());
                }
              }
            }
          }

          template.append(buildParams(headers));
          template.append(buildParams(uriParameters));
          template.append(buildParams(queryParameters));
          template.append("\n  ").append("request: ");

          if (!request.payloads().isEmpty()) {
            template.append("\n    ").append("body: ");
            template.append("\n      ").append("expression: ");
          }

          if (!headers.isEmpty()) {
            template.append("\n    ").append("header: ");
            for (Parameter header : headers) {
              template.append("\n      ").append(header.name().value()).append(":");
              template.append("\n        ").append("value").append(": \"#[parameters['").append(header.name().value()).append("']]\"");
            }
          }

          if (!queryParameters.isEmpty()) {
            template.append("\n    ").append("queryParameter: ");
            for (Parameter queryParam : queryParameters) {
              template.append("\n      ").append(queryParam.name().value()).append(": ");
              template.append("\n        ").append("value").append(": \"#[parameters['").append(queryParam.name().value()).append("']]\"");
            }
          }

          if (!uriParameters.isEmpty()) {
            template.append("\n    ").append("uriParameter: ");
            for (Parameter uriParameter : uriParameters) {
              template.append("\n      ").append(uriParameter.name().value()).append(":");
              template.append("\n        ").append("value").append(": \"#[parameters['").append(uriParameter.name().value()).append("']]\"");
            }
          }
        }

        template.append("\n  ").append("response: ");
        template.append("\n    ").append("body: \"#[payload]\"");


        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "rest_sdk_suggest", template.toString());
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
  private String buildParams(List<Parameter> queryParameters) {
    final StringBuilder queryParams = new StringBuilder();
    for (Parameter queryParam : queryParameters) {
      final Shape schema = queryParam.schema();
      queryParams.append("\n").append("    ").append(queryParam.name().value()).append(" : ");
      queryParams.append("\n").append("      ").append("type").append(": ").append(toSchemaName(schema));
      queryParams.append("\n").append("      ").append("displayName").append(": ").append(queryParam.name().value());
      queryParams.append("\n").append("      ").append("required").append(": ").append(queryParam.required());
      if (!queryParam.description().isNullOrEmpty()) {
        queryParams.append("\n").append("      ").append("description").append(": ").append("\"").append(queryParam.description().value()).append("\"");
      }
    }
    return queryParams.toString();
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


}
