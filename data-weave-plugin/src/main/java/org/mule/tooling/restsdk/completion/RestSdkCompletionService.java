package org.mule.tooling.restsdk.completion;


import amf.client.model.domain.*;
import amf.core.model.DataType;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.daemon.impl.quickfix.EmptyExpression;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;
import webapi.WebApiDocument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mule.tooling.restsdk.utils.MapUtils.map;
import static org.mule.tooling.restsdk.utils.RestSdkHelper.parseWebApi;
import static org.mule.tooling.restsdk.utils.RestSdkPaths.OPERATION_PATH;

public class RestSdkCompletionService {


  public static final String SCHEMAS_FOLDER = "schemas";
  public static final String OK_STATUS = "200";
  Map<String, String> SIMPLE_TYPE_MAP = map(
          DataType.String(), "string",
          DataType.Boolean(), "boolean",
          DataType.Number(), "number",
          DataType.Decimal(), "number",
          DataType.Integer(), "integer",
          DataType.Float(), "number",
          DataType.Double(), "number",
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
    final SelectionPath yamlPath = SelectionPath.pathOfYaml(parentElement);
    if (yamlPath.matches(RestSdkPaths.OPERATION_BASE_PATH) ||
            yamlPath.matches(OPERATION_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_QUERY_PARAMS_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_PATH) ||
            yamlPath.matches(RestSdkPaths.OPERATION_URI_PARAMS_PATH)) {
      suggestOnOperations(completionParameters, project, result, yamlPath);
    }
    return result;
  }

  private void suggestOnOperations(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, SelectionPath yamlPath) {
    final WebApiDocument webApiDocument = parseWebApi(completionParameters.getOriginalFile());
    if (webApiDocument != null) {
      WebApi webApi = (WebApi) webApiDocument.encodes();
      if (yamlPath.matches(RestSdkPaths.OPERATION_BASE_PATH)) {
        suggestOperationBase(result, webApi);
      } else if (yamlPath.matches(OPERATION_PATH)) {
        suggestOperationTemplate(project, completionParameters.getOriginalFile(), result, webApi);
      } else {
        PsiElement base = RestSdkPaths.RELATIVE_OPERATION_BASE_FROM_REQUEST_PATH.selectYaml(completionParameters.getPosition());
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

  private void suggestOperationTemplate(Project project, PsiFile file, ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + " (Scaffold New Operation)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation, endpoint), true);
        final List<Resource> resources = new ArrayList<>();
        final StringBuilder template =
                new StringBuilder("$name$:\n" +
                        "  description: $description$\n" +
                        "  displayName: $name$\n" +
                        "  base: " + operation.name() + "\n" +
                        "  parameters: ");

        final Request request = operation.request();
        if (request != null) {
          final List<Payload> payloads = request.payloads();

          if (!payloads.isEmpty()) {
            final Shape schema = payloads.get(0).schema();
            if (schema instanceof NodeShape) {
              List<PropertyShape> properties = ((NodeShape) schema).properties();
              for (PropertyShape property : properties) {
                template.append("\n").append("    ").append(property.name().value()).append(": ");
                template.append("\n").append("      ").append(toSchemaName(property.range(), resources));
                template.append("\n").append("      ").append("displayName").append(": ").append(property.name().value());
                template.append("\n").append("      ").append("required").append(": ").append(property.minCount().value() == 0);
                if (!property.description().isNullOrEmpty()) {
                  template.append("\n").append("      ").append("description").append(": ").append(property.description().value());
                }
              }
            } else {
              template.append("\n").append("    ").append("body").append(": ");
              template.append("\n").append("      ").append(toSchemaName(schema, resources));
            }
          }

          final List<Parameter> headers = request.headers();
          final List<Parameter> uriParameters = request.uriParameters();
          final List<Parameter> queryParameters = request.queryParameters();

          template.append(buildParams(headers, resources));
          template.append(buildParams(uriParameters, resources));
          template.append(buildParams(queryParameters, resources));
          template.append("\n  ").append("request: ");
          if (!payloads.isEmpty()) {
            template.append("\n    ").append("body: ");
            template.append("\n      ").append("expression: \"#[");
            final Shape schema = payloads.get(0).schema();
            if (schema instanceof NodeShape) {
              template.append("\n").append("        ").append("{\n");
              List<PropertyShape> properties = ((NodeShape) schema).properties();
              for (PropertyShape property : properties) {
                template.append("          ").append("(parameters.&'").append(property.name().value()).append("'),").append("\n");
              }
              template.append("        ").append("}");
            } else {
              template.append("\n").append("    ").append("body").append(": ");
              template.append("\n").append("      ").append(toSchemaName(schema, resources));
            }
            template.append("\n      ").append("]\"");
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

        List<Response> responses = operation.responses();
        if (!responses.isEmpty()) {
          Response response = responses.stream().filter((r) -> OK_STATUS.equals(r.statusCode().value())).findFirst().orElse(null);
          if (response != null && !response.payloads().isEmpty()) {
            Payload payload = response.payloads().get(0);
            template.append("\n  ").append("response: ");
            template.append("\n    ").append("body:");
            template.append("\n      ").append("value: \"#[payload]\"");
            template.append("\n      ").append(toSchemaName(payload.schema(), resources));
          }
        }

        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "rest_sdk_suggest", template.toString());
        myTemplate.addVariable("name", new TextExpression("My" + StringUtils.capitalize(operation.name().value())), true);
        myTemplate.addVariable("description", operation.description().isNullOrEmpty() ? new EmptyExpression() : new ConstantNode(operation.description().value()), true);

        elementBuilder = elementBuilder.withInsertHandler((context, item1) -> {
          final int selectionStart = context.getEditor().getCaretModel().getOffset();
          final int startOffset = context.getStartOffset();
          final int tailOffset = context.getTailOffset();
          context.getDocument().deleteString(startOffset, tailOffset);
          context.setAddCompletionChar(false);
          TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
          generateResources(file, resources);
        });

        result.add(elementBuilder);
      });
    });
  }

  private void generateResources(PsiFile file, List<Resource> resources) {
    final VirtualFile containerFolder = file.getVirtualFile().getParent();
    if (!resources.isEmpty()) {
      try {
        final VirtualFile resourcesFolder;
        if (containerFolder.findChild(SCHEMAS_FOLDER) == null) {
          resourcesFolder = containerFolder.createChildDirectory(this, SCHEMAS_FOLDER);
        } else {
          resourcesFolder = containerFolder.findChild(SCHEMAS_FOLDER);
        }
        resources.forEach((resource) -> {
          try {
            assert resourcesFolder != null;
            resourcesFolder.createChildData(this, resource.name).setBinaryContent(resource.content.getBytes(StandardCharsets.UTF_8));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @NotNull
  private String buildParams(List<Parameter> queryParameters, List<Resource> resources) {
    final StringBuilder queryParams = new StringBuilder();
    for (Parameter queryParam : queryParameters) {
      final Shape schema = queryParam.schema();
      queryParams.append("\n").append("    ").append(queryParam.name().value()).append(" : ");
      queryParams.append("\n").append("      ").append(toSchemaName(schema, resources));
      queryParams.append("\n").append("      ").append("displayName").append(": ").append(queryParam.name().value());
      queryParams.append("\n").append("      ").append("required").append(": ").append(queryParam.required());
      if (!queryParam.description().isNullOrEmpty()) {
        queryParams.append("\n").append("      ").append("description").append(": ").append("\"").append(queryParam.description().value()).append("\"");
      }
    }
    return queryParams.toString();
  }

  private String toSchemaName(Shape schema, List<Resource> resources) {
    StringBuilder result = new StringBuilder();
    if (schema instanceof ScalarShape) {
      return result.append("type").append(": ").append(SIMPLE_TYPE_MAP.get(((ScalarShape) schema).dataType().value())).toString();
    } else if (schema instanceof AnyShape) {
      final String toJsonSchema = ((AnyShape) schema).toJsonSchema();
      final String fileName = schema.name().value() + ".json";
      resources.add(new Resource(fileName, toJsonSchema));
      return result.append("typeSchema").append(": ").append("./").append(SCHEMAS_FOLDER).append("/").append(fileName).toString();
    } else {
      return "";
    }
  }

  private void suggestOperationBase(ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name());
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation, endpoint), true);
        result.add(elementBuilder);
      });
    });
  }

  private String operationType(Operation operation, EndPoint endpoint) {
    return operation.method().value().toUpperCase() + "-" + endpoint.path().value();
  }

  static class Resource {
    String name;
    String content;

    public Resource(String name, String content) {
      this.name = name;
      this.content = content;
    }

    public String getName() {
      return name;
    }

    public String getContent() {
      return content;
    }
  }

}
