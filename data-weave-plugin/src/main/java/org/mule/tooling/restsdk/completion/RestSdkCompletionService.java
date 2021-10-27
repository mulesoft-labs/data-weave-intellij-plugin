package org.mule.tooling.restsdk.completion;


import amf.apicontract.client.platform.AMFElementClient;
import amf.apicontract.client.platform.OASConfiguration;
import amf.apicontract.client.platform.model.domain.*;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.platform.model.document.Document;
import amf.core.client.platform.model.domain.PropertyShape;
import amf.core.client.platform.model.domain.Shape;
import amf.core.client.scala.model.DataType;
import amf.shapes.client.platform.model.domain.AnyShape;
import amf.shapes.client.platform.model.domain.NodeShape;
import amf.shapes.client.platform.model.domain.ScalarShape;
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
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mule.tooling.restsdk.utils.MapUtils.map;
import static org.mule.tooling.restsdk.utils.RestSdkHelper.parseWebApi;
import static org.mule.tooling.restsdk.utils.RestSdkPaths.OPERATION_PATH;
import static org.mule.tooling.restsdk.utils.RestSdkPaths.TRIGGERS_PATH;

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
    } else if (yamlPath.matches(TRIGGERS_PATH)
            || yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_QUERY_PARAMS_PATH)
            || yamlPath.matches(RestSdkPaths.TRIGGERS_PATH_PATH)
            || yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_HEADER_PATH)
            || yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_URI_PARAMETER_PATH)) {
      suggestOnTriggers(completionParameters, project, result, yamlPath);
    } else if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_PATH) ||
            yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_PATH_PATH) ||
            yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_QUERY_PARAMS_PATH) ||
            yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_URI_PARAMETER_PATH) ||
            yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_HEADER_PATH)
    ) {
      suggestOnSampleData(completionParameters, project, result, yamlPath);
    } else if (yamlPath.getName().equals("path")) {
      final PsiFile yamlFile = completionParameters.getOriginalFile();
      final Document webApiDocument = parseWebApi(yamlFile);
      if (webApiDocument != null) {
        WebApi webApi = (WebApi) webApiDocument.encodes();
        suggestPaths(result, webApi);
      }
    }
    return result;
  }

  private void suggestOnSampleData(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, SelectionPath yamlPath) {
    final PsiFile yamlFile = completionParameters.getOriginalFile();
    final Document webApiDocument = parseWebApi(yamlFile);
    if (webApiDocument != null) {
      WebApi webApi = (WebApi) webApiDocument.encodes();
      if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_PATH)) {
        suggestSampleDataTemplate(project, result, yamlFile, webApi);
      } else if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_PATH_PATH)) {
        suggestPaths(result, webApi);
      } else {
        final PsiElement path = RestSdkPaths.RELATIVE_TRIGGER_PATH_FROM_BINDING_PATH.selectYaml(completionParameters.getPosition());
        final PsiElement method = RestSdkPaths.RELATIVE_TRIGGER_METHOD_FROM_BINDING_PATH.selectYaml(completionParameters.getPosition());
        if (path instanceof YAMLScalar && method instanceof YAMLScalar) {
          final String pathText = ((YAMLScalar) path).getTextValue();
          final String methodText = ((YAMLScalar) method).getTextValue();
          final Operation operation = RestSdkHelper.operationByMethodPath((WebApi) webApiDocument.encodes(), methodText, pathText);
          if (operation != null) {
            if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_QUERY_PARAMS_PATH)) {
              suggestQueryParams(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_HEADER_PATH)) {
              suggestHeaders(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_URI_PARAMETER_PATH)) {
              suggestUriParams(result, operation);
            }
          }
        }
      }
    }
  }

  private void suggestPaths(ArrayList<LookupElement> result, WebApi webApi) {
    webApi.endPoints().forEach((endpoint) -> {
      LookupElementBuilder elementBuilder = LookupElementBuilder.create(endpoint.path().value());
      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
      result.add(elementBuilder);
    });
  }

  private void suggestOnTriggers(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, SelectionPath yamlPath) {
    final PsiFile yamlFile = completionParameters.getOriginalFile();
    final Document webApiDocument = parseWebApi(yamlFile);
    if (webApiDocument != null) {
      WebApi webApi = (WebApi) webApiDocument.encodes();
      if (yamlPath.matches(TRIGGERS_PATH)) {
        suggestTriggerTemplate(project, result, yamlFile, webApi);
      } else if (yamlPath.matches(RestSdkPaths.TRIGGERS_PATH_PATH)) {
        suggestPaths(result, webApi);
      } else {
        final PsiElement path = RestSdkPaths.RELATIVE_TRIGGER_PATH_FROM_BINDING_PATH.selectYaml(completionParameters.getPosition());
        final PsiElement method = RestSdkPaths.RELATIVE_TRIGGER_METHOD_FROM_BINDING_PATH.selectYaml(completionParameters.getPosition());
        if (path instanceof YAMLScalar && method instanceof YAMLScalar) {
          final String pathText = ((YAMLScalar) path).getTextValue();
          final String methodText = ((YAMLScalar) method).getTextValue();
          final Operation operation = RestSdkHelper.operationByMethodPath((WebApi) webApiDocument.encodes(), methodText, pathText);
          if (operation != null) {
            if (yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_QUERY_PARAMS_PATH)) {
              suggestQueryParams(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_HEADER_PATH)) {
              suggestHeaders(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.TRIGGERS_BINDING_URI_PARAMETER_PATH)) {
              suggestUriParams(result, operation);
            }
          }
        }
      }
    }
  }

  private void suggestSampleDataTemplate(Project project, ArrayList<LookupElement> result, PsiFile yamlFile, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + " (Scaffold New SampleData)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation, endpoint), true);
        final List<Resource> resources = new ArrayList<>();
        final StringBuilder template = new StringBuilder();
        template.append("$name$:\n");

        final Request request = operation.request();
        if (request != null) {
          template.append("  ").append("parameters: ").append("\n");
        }
        template.append("  ").append("definition: ").append("\n");
        template.append("    ").append("type: ").append("http").append("\n");
        template.append("    ").append("request: ").append("\n");
        template.append("      ").append("method: ").append(operation.method()).append("\n");
        template.append("      ").append("path: ").append(endpoint.path()).append("\n");

        if (request != null) {
          template.append("      ").append("binding: ").append("\n");
          final List<Parameter> queryParameters = operation.request().queryParameters();
          if (!queryParameters.isEmpty()) {
            template.append("        ").append("queryParameter: ").append("\n");
          }
          final List<Parameter> uriParameters = operation.request().uriParameters();
          if (!uriParameters.isEmpty()) {
            template.append("        ").append("uriParameter: ").append("\n");
            for (Parameter uriParameter : uriParameters) {
              template.append("          ").append(uriParameter.name().value()).append(":").append("\n");
              template.append("            ").append("value").append(": \"#[]\"").append("\n");
            }
          }
        }
        template.append("    ").append("transform: ").append("\n");
        template.append("      ").append("expression: \"#[payload]\"").append("\n");

        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "rest_sdk_suggest", template.toString());
        myTemplate.addVariable("name", new TextExpression(StringUtils.capitalize(operation.name().value()) + "SampleData"), true);

        elementBuilder = elementBuilder.withInsertHandler((context, item1) -> {
          final int selectionStart = context.getEditor().getCaretModel().getOffset();
          final int startOffset = context.getStartOffset();
          final int tailOffset = context.getTailOffset();
          context.getDocument().deleteString(startOffset, tailOffset);
          context.setAddCompletionChar(false);
          TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
          generateResources(yamlFile, resources);
        });

        result.add(elementBuilder);
      });
    });
  }

  private void suggestTriggerTemplate(Project project, ArrayList<LookupElement> result, PsiFile yamlFile, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + " (Scaffold New Trigger)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation, endpoint), true);
        final List<Resource> resources = new ArrayList<>();
        final StringBuilder template = new StringBuilder();
        template.append("$name$:\n")
                .append("  ").append("description: $description$\n")
                .append("  ").append("displayName: $name$\n");

        final Request request = operation.request();
        if (request != null) {
          template.append(parametersTemplate(resources, request));
        }

        template.append("  ").append("method: ").append(operation.method()).append("\n");
        template.append("  ").append("path: ").append(endpoint.path()).append("\n");

        if (!operation.responses().isEmpty()) {
          List<Payload> payloads = operation.responses().get(0).payloads();
          if (!payloads.isEmpty()) {
            template.append("  ").append(toOutputSchema(payloads.get(0).schema(), resources)).append("\n");
            template.append("  ").append("outputMediaType: application/json").append("\n");
          }
        }
        if (request != null) {
          template.append("  ").append("binding: ").append("\n");
          template.append(buildOperationRequestTemplate(resources, request, "    "));
        }
        template.append("#").append("Extract the items the collection of items form the http response").append("\n");
        template.append("  ").append("items:").append("\n");
        template.append("    ").append("extraction:").append("\n");
        template.append("      ").append("expression:").append(" \"#[payload]\"").append("\n");

        template.append("#").append("Extract the Watermark expression from each item.").append("\n");
        template.append("  ").append("watermark:").append("\n");
        template.append("    ").append("extraction:").append("\n");
        template.append("      ").append("expression:").append(" \"#[item]\"").append("\n");

        template.append("#").append("Specify the expression to identify the elements.").append("\n");
        template.append("  ").append("identity:").append("\n");
        template.append("    ").append("extraction:").append("\n");
        template.append("      ").append("expression:").append(" \"#[item]\"").append("\n");

        final Template myTemplate = TemplateManager.getInstance(project).createTemplate("template", "rest_sdk_suggest", template.toString());
        myTemplate.addVariable("name", new TextExpression("On" + StringUtils.capitalize(operation.name().value())), true);
        myTemplate.addVariable("description", operation.description().isNullOrEmpty() ? new EmptyExpression() : new ConstantNode(operation.description().value()), true);

        elementBuilder = elementBuilder.withInsertHandler((context, item1) -> {
          final int selectionStart = context.getEditor().getCaretModel().getOffset();
          final int startOffset = context.getStartOffset();
          final int tailOffset = context.getTailOffset();
          context.getDocument().deleteString(startOffset, tailOffset);
          context.setAddCompletionChar(false);
          TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), myTemplate);
          generateResources(yamlFile, resources);
        });

        result.add(elementBuilder);
      });
    });
  }

  private String parametersTemplate(List<Resource> resources, Request request) {
    final List<Payload> payloads = request.payloads();
    final List<Parameter> headers = request.headers();
    final List<Parameter> uriParameters = request.uriParameters();
    final List<Parameter> queryParameters = request.queryParameters();
    final StringBuilder template = new StringBuilder();
    template.append("  ").append("parameters").append(":").append("\n");
    if (!payloads.isEmpty()) {
      final Shape schema = payloads.get(0).schema();
      if (schema instanceof NodeShape) {
        List<PropertyShape> properties = ((NodeShape) schema).properties();
        for (PropertyShape property : properties) {
          template.append("    ").append(property.name().value()).append(": \n");
          template.append("      ").append(toSchemaName(property.range(), resources)).append("\n");
          template.append("      ").append("displayName").append(": ").append(property.name().value()).append("\n");
          template.append("      ").append("required").append(": ").append(property.minCount().value() == 0).append("\n");
          if (!property.description().isNullOrEmpty()) {
            template.append("      ").append("description").append(": ").append(property.description().value()).append("\n");
          }
        }
      } else {
        template.append("    ").append("body").append(": ");
        template.append("      ").append(toSchemaName(schema, resources)).append("\n");
      }
    }

    template.append(buildParams(headers, resources));
    template.append(buildParams(uriParameters, resources));
    template.append(buildParams(queryParameters, resources));
    return template.toString();
  }

  private void suggestOnOperations(CompletionParameters completionParameters, Project project, ArrayList<LookupElement> result, SelectionPath yamlPath) {
    final Document webApiDocument = parseWebApi(completionParameters.getOriginalFile());
    if (webApiDocument != null) {
      WebApi webApi = (WebApi) webApiDocument.encodes();
      if (yamlPath.matches(RestSdkPaths.OPERATION_BASE_PATH)) {
        suggestOperationBase(result, webApi);
      } else if (yamlPath.matches(OPERATION_PATH)) {
        suggestOperationTemplate(project, completionParameters.getOriginalFile(), result, webApi);
      } else {
        PsiElement base = RestSdkPaths.RELATIVE_OPERATION_BASE_FROM_REQUEST_PATH.selectYaml(completionParameters.getPosition());
        if (base instanceof YAMLScalar) {
          final String baseOperation = ((YAMLScalar) base).getTextValue();
          final Operation operation = RestSdkHelper.operationById((WebApi) webApiDocument.encodes(), baseOperation);
          if (operation != null) {
            if (yamlPath.matches(RestSdkPaths.OPERATION_QUERY_PARAMS_PATH)) {
              suggestQueryParams(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_PATH)) {
              suggestHeaders(result, operation);
            } else if (yamlPath.matches(RestSdkPaths.OPERATION_URI_PARAMS_PATH)) {
              suggestUriParams(result, operation);
            }
          }
        }
      }
    }
  }

  private void suggestUriParams(ArrayList<LookupElement> result, Operation maybeOperation) {
    List<Parameter> queryParameters = maybeOperation.request().uriParameters();
    queryParameters.forEach((queryParam) -> {
      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
      result.add(elementBuilder);
    });
  }

  private void suggestHeaders(ArrayList<LookupElement> result, Operation maybeOperation) {
    List<Parameter> queryParameters = maybeOperation.request().headers();
    queryParameters.forEach((queryParam) -> {
      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":");
      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
      result.add(elementBuilder);
    });
  }

  private void suggestQueryParams(ArrayList<LookupElement> result, Operation operation) {
    List<Parameter> queryParameters = operation.request().queryParameters();
    queryParameters.forEach((queryParam) -> {
      LookupElementBuilder elementBuilder = LookupElementBuilder.create(queryParam.name() + ":" + "\n");
      elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
      result.add(elementBuilder);
    });
  }

  private void suggestOperationTemplate(Project project, PsiFile file, ArrayList<LookupElement> result, WebApi webApi) {
    final List<EndPoint> endPoints = webApi.endPoints();
    endPoints.forEach((endpoint) -> {
      endpoint.operations().forEach((operation) -> {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(operation.name() + " (Scaffold New Operation)");
        elementBuilder = elementBuilder.withIcon(AllIcons.Nodes.Property);
        elementBuilder = elementBuilder.withTypeText(operationType(operation, endpoint), true);
        final List<Resource> resources = new ArrayList<>();
        final StringBuilder template = new StringBuilder();
        template.append("$name$:\n")
                .append("  ").append("description: $description$\n")
                .append("  ").append("displayName: $name$\n")
                .append("  ").append("base: ").append(operation.name().value()).append("\n");


        final Request request = operation.request();
        if (request != null) {
          template.append(parametersTemplate(resources, request));
          template.append("  ").append("request: ").append("\n");
          template.append(buildOperationRequestTemplate(resources, request, "    "));
        }

        List<Response> responses = operation.responses();
        if (!responses.isEmpty()) {
          Response response = responses.stream().filter((r) -> OK_STATUS.equals(r.statusCode().value())).findFirst().orElse(null);
          if (response != null && !response.payloads().isEmpty()) {
            Payload payload = response.payloads().get(0);
            template.append("  ").append("response: ").append("\n");
            template.append("    ").append("body:").append("\n");
            template.append("      ").append("value: \"#[payload]\"").append("\n");
            template.append("      ").append(toSchemaName(payload.schema(), resources)).append("\n");
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

  private String buildOperationRequestTemplate(List<Resource> resources, Request request, String indentation) {
    final List<Payload> payloads = request.payloads();
    final List<Parameter> headers = request.headers();
    final List<Parameter> uriParameters = request.uriParameters();
    final List<Parameter> queryParameters = request.queryParameters();
    StringBuilder template = new StringBuilder();
    if (!payloads.isEmpty()) {
      template.append("    ").append("body: ").append("\n");
      template.append("      ").append("expression: \"#[").append("\n");
      final Shape schema = payloads.get(0).schema();
      if (schema instanceof NodeShape) {
        template.append("        ").append("{\n");
        List<PropertyShape> properties = ((NodeShape) schema).properties();
        for (PropertyShape property : properties) {
          template.append("          ").append("(parameters.&'").append(property.name().value()).append("'),").append("\n");
        }
        template.append("        ").append("}").append("\n");
      } else {
        template.append("    ").append("body").append(": ").append("\n");
        template.append("      ").append(toSchemaName(schema, resources)).append("\n");
      }
      template.append("      ").append("]\"").append("\n");
    }

    if (!headers.isEmpty()) {
      template.append("    ").append("header: ").append("\n");
      for (Parameter header : headers) {
        template.append("      ").append(header.name().value()).append(":").append("\n");
        template.append("        ").append("value").append(": \"#[parameters['").append(header.name().value()).append("']]\"").append("\n");
      }
    }

    if (!queryParameters.isEmpty()) {
      template.append("    ").append("queryParameter: ").append("\n");
      for (Parameter queryParam : queryParameters) {
        template.append("      ").append(queryParam.name().value()).append(": ").append("\n");
        template.append("        ").append("value").append(": \"#[parameters['").append(queryParam.name().value()).append("']]\"").append("\n");
      }
    }

    if (!uriParameters.isEmpty()) {
      template.append("    ").append("uriParameter: ").append("\n");
      for (Parameter uriParameter : uriParameters) {
        template.append("      ").append(uriParameter.name().value()).append(":").append("\n");
        template.append("        ").append("value").append(": \"#[parameters['").append(uriParameter.name().value()).append("']]\"").append("\n");
      }
    }
    return template.toString();
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
      queryParams.append("    ").append(queryParam.name().value()).append(" : ").append("\n");
      queryParams.append("      ").append(toSchemaName(schema, resources)).append("\n");
      queryParams.append("      ").append("displayName").append(": ").append(queryParam.name().value()).append("\n");
      queryParams.append("      ").append("required").append(": ").append(queryParam.required()).append("\n");
      if (!queryParam.description().isNullOrEmpty()) {
        queryParams.append("      ").append("description").append(": ").append("\"").append(queryParam.description().value()).append("\"").append("\n");
      }
    }
    return queryParams.toString();
  }

  private String toSchemaName(Shape schema, List<Resource> resources) {
    StringBuilder result = new StringBuilder();
    if (schema instanceof ScalarShape) {
      return result.append("type").append(": ").append(SIMPLE_TYPE_MAP.get(((ScalarShape) schema).dataType().value())).toString();
    } else if (schema instanceof AnyShape) {
      final AMFElementClient client = OASConfiguration.OAS20().elementClient();
      final String toJsonSchema = client.toJsonSchema((AnyShape) schema);
      final String fileName = schema.name().value() + ".json";
      resources.add(new Resource(fileName, toJsonSchema));
      return result.append("typeSchema").append(": ").append("./").append(SCHEMAS_FOLDER).append("/").append(fileName).toString();
    } else {
      return "";
    }
  }

  private String toOutputSchema(Shape schema, List<Resource> resources) {
    StringBuilder result = new StringBuilder();
    if (schema instanceof AnyShape) {
      final AMFElementClient client = OASConfiguration.OAS20().elementClient();
      final String toJsonSchema = client.toJsonSchema((AnyShape) schema);
      final String fileName = schema.name().value() + ".json";
      resources.add(new Resource(fileName, toJsonSchema));
      return result.append("outputType").append(": ").append("./").append(SCHEMAS_FOLDER).append("/").append(fileName).toString();
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
