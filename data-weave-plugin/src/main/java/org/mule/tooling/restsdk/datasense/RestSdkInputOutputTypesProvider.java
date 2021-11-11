package org.mule.tooling.restsdk.datasense;


import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.platform.model.document.Document;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.lang.dw.injector.YamlLanguageInjector;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.InputOutputTypesProvider;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;
import org.mule.tooling.lang.dw.service.WeaveToolingService;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.tooling.restsdk.utils.JsonSchemaConverter;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.SelectionPath;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.parser.ast.QName;
import org.mule.weave.v2.ts.*;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.Builder;

import java.util.Optional;

import static org.mule.tooling.restsdk.utils.RestSdkHelper.isInRestSdkContextFile;
import static org.mule.tooling.restsdk.utils.SelectionPath.pathOfYaml;

public class RestSdkInputOutputTypesProvider implements InputOutputTypesProvider {
  // Bindings
  public static final String PARAMETERS_KEY = "parameters";
  public static final String WATERMARK_KEY = "watermark";
  public static final String PAYLOAD_KEY = "payload";
  public static final String ITEM_KEY = "item";
  public static final String ATTRIBUTES_KEY = "attributes";
  public static final String LINK_KEY = "link";
  public static final String OPERATION_ID_KEY = "operationId";
  public static final String METHOD_KEY = "method";
  public static final String PATH_KEY = "path";
  public static final String SUMMARY_KEY = "summary";

  @Override
  public boolean support(PsiFile psiFile) {
    return isInRestSdkContextFile(psiFile);
  }

  @Override
  public @NotNull ImplicitInput inputTypes(PsiFile psiFile) {
    final ImplicitInput implicitInput = new ImplicitInput();
    final PsiElement context = psiFile.getContext();
    if (context != null) {
      final SelectionPath path = pathOfYaml(context);
      if (path.matches(RestSdkPaths.OPERATION_IDENTIFIER_PATH)) {
        implicitInput.addInput(OPERATION_ID_KEY, new StringType(Option.empty()));
        implicitInput.addInput(METHOD_KEY, new StringType(Option.empty()));
        implicitInput.addInput(PATH_KEY, new StringType(Option.empty()));
      } else if (path.matches(RestSdkPaths.PAGINATION_PATH) || path.matches(RestSdkPaths.SECURITY_VALIDATION_PATH)
              || path.matches(RestSdkPaths.SECURITY_ERROR_TEMPLATE_PATH) || path.matches(RestSdkPaths.SECURITY_REFRESH_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_SAMPLE_DATA_PATH)) {
        createPayloadWithAttributesInputs(implicitInput);
      } else if (path.matches(RestSdkPaths.TRIGGERS_BINDING_BODY_EXPRESSION)) {
        createTriggersBinding(implicitInput, context, RestSdkPaths.RELATIVE_TRIGGER_PARAMETERS_SELECTOR_FROM_BINDING_BODY_PATH);
      } else if (path.matches(RestSdkPaths.TRIGGERS_BINDING_QUERY_PARAMS_EXPRESSION_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_BINDING_URI_PARAMETER_EXPRESSION_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_BINDING_HEADER_EXPRESSION_PATH)) {
        createTriggersBinding(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER_PATH);
      } else if (path.matches(RestSdkPaths.TRIGGERS_WATERMARK_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_IDENTITY_EXTRACTION_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_EVENT_PATH)) {
        createTriggersWatermarkExtractionBinding(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_ITEMS_PATH);
      } else if (path.matches(RestSdkPaths.TRIGGERS_ITEMS_PATH)) {
        createTriggersItemsBinding(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_ITEMS_PATH);
      } else if (path.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PATH)) {
        createSampleDataRequestBinding(implicitInput, context, RestSdkPaths.RELATIVE_GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PARAMETERS_PATH);
      } else if (path.matches(RestSdkPaths.GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PATH)) {
        createSampleDataTransformBinding(implicitInput, context, RestSdkPaths.RELATIVE_GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PARAMETERS_PATH);
      } else if (path.matches(RestSdkPaths.PAGINATION_PARAMETERS)) {
        createPaginationParametersInputs(implicitInput);
      } else if (path.matches(RestSdkPaths.OPERATION_REQUEST_BODY_PATH)) {
        createOperationRequest(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_BODY_REQUEST_PATH);
      } else if (path.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_EXPRESSION_PATH)
              || path.matches(RestSdkPaths.OPERATION_URI_PARAMS_EXPRESSION_PATH)
              || path.matches(RestSdkPaths.OPERATION_REQUEST_HEADER_EXPRESSION_PATH)) {
        createOperationRequest(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER_PATH);
      } else if (path.matches(RestSdkPaths.OPERATION_DISPLAY_NAME_PATH)) {
        implicitInput.addInput(OPERATION_ID_KEY, new StringType(Option.empty()));
        implicitInput.addInput(METHOD_KEY, new StringType(Option.empty()));
        implicitInput.addInput(PATH_KEY, new StringType(Option.empty()));
        implicitInput.addInput(SUMMARY_KEY, new StringType(Option.empty()));
      } else if (path.matches(RestSdkPaths.TEST_CONNECTION_PATH)) {
        createTestConnectionInputs(implicitInput, context);
      } else if (path.matches(RestSdkPaths.VALUE_PROVIDERS_ITEMS_EXTRACTION_EXPRESSION_PATH)) {
        createValueProviderExtraction(implicitInput, context);
      } else if (path.matches(RestSdkPaths.VALUE_PROVIDERS_ITEMS_DISPLAY_NAME_EXPRESSION_PATH)
              || path.matches(RestSdkPaths.VALUE_PROVIDERS_ITEMS_VALUE_EXPRESSION_PATH)) {
        createValueProviderWithItems(implicitInput, context);
      } else if (path.matches(RestSdkPaths.VALUE_PROVIDERS_REQUEST)) {
        createValueProviderRequest(implicitInput, context);
      } else if (path.matches(RestSdkPaths.OPERATION_RESPONSE_BODY_PATH)) {
        createOperationResponse(implicitInput, context);
      } else {
        createPayloadWithAttributesInputs(implicitInput);
      }
    }

    final ImplicitInput sampleImplicits = loadSampleImplicits(psiFile);
    if (sampleImplicits != null) {
      //Merge With sample data
      final scala.collection.Iterator<String> mayBePayload = sampleImplicits.implicitInputs().keySet().iterator();
      while (mayBePayload.hasNext()) {
        String inputName = mayBePayload.next();
        implicitInput.addInput(inputName, sampleImplicits.implicitInputs().apply(inputName));
      }
    }
    return implicitInput;
  }

  private void createValueProviderExtraction(ImplicitInput implicitInput, PsiElement context) {
    final SelectionPath request = SelectionPath.PARENT.parent().parent().child("request");
    final WeaveType payloadType = resolveOperationResponseType(context, request.child("path"), request.child("method")).orElse(new AnyType());
    final SelectionPath parametersSelector = SelectionPath.PARENT.parent().parent().parent().child("parameters");
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
  }

  private void createValueProviderRequest(ImplicitInput implicitInput, PsiElement context) {
    final SelectionPath parametersSelector = SelectionPath.PARENT.parent().parent().parent().parent().parent().child("parameters");
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
  }

  private void createValueProviderWithItems(ImplicitInput implicitInput, PsiElement context) {
    final SelectionPath request = SelectionPath.PARENT.parent().parent().child("request");
    final WeaveType payloadType = resolveOperationResponseType(context, request.child("path"), request.child("method")).orElse(new AnyType());
    final SelectionPath parametersSelector = SelectionPath.PARENT.parent().parent().parent().child("parameters");
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
    final SelectionPath itemsExtractionExpression = SelectionPath.PARENT.parent().child("items").child("extraction").child("expression");
    final PsiElement itemsExtractionElement = itemsExtractionExpression.selectYaml(context);
    WeaveType itemType = new AnyType();
    if (itemsExtractionElement instanceof YAMLScalar) {
      final String itemsExtractionTextExpression = ((YAMLScalar) itemsExtractionElement).getTextValue();
      final Project project = context.getProject();
      final String weaveItemsExpression = YamlLanguageInjector.extractWeaveExpression(itemsExtractionTextExpression);
      final ImplicitInput itemImplicitInput = new ImplicitInput();
      itemImplicitInput.addInput(PAYLOAD_KEY, payloadType);
      itemImplicitInput.addInput(PARAMETERS_KEY, parametersType);
      WeaveType weaveType = WeaveToolingService.getInstance(project).typeOf(weaveItemsExpression, itemImplicitInput);
      if (weaveType != null) {
        itemType = arrayTypeOf(weaveType);
      }
    }
    implicitInput.addInput(ITEM_KEY, itemType);
  }

  private void createTriggersBinding(ImplicitInput implicitInput, PsiElement context, SelectionPath parameterSelector) {
    ObjectType weaveType = loadParametersType(context, parameterSelector);
    implicitInput.addInput(WATERMARK_KEY, new DateTimeType());
    implicitInput.addInput(PARAMETERS_KEY, weaveType);
  }

  private void createTestConnectionInputs(ImplicitInput implicitInput, PsiElement context) {
    final SelectionPath path = SelectionPath.PARENT.parent().parent().child("path");
    final SelectionPath method = SelectionPath.PARENT.parent().parent().child("method");
    final WeaveType payloadType = resolveOperationResponseType(context, path, method).orElse(new AnyType());
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(ATTRIBUTES_KEY, createHttpAttributes());
  }

  private ImplicitInput loadSampleImplicits(PsiFile psiFile) {
    final WeaveRuntimeService instance = WeaveRuntimeService.getInstance(psiFile.getProject());
    final WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(psiFile));
    return weaveDocument != null ? instance.getImplicitInputTypes(weaveDocument) : null;
  }


  private WeaveType loadSampleOutput(PsiFile psiFile) {
    final WeaveRuntimeService instance = WeaveRuntimeService.getInstance(psiFile.getProject());
    final WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(psiFile));
    return weaveDocument != null ? instance.getExpectedOutput(weaveDocument) : null;
  }

  private void createOperationRequest(ImplicitInput implicitInput, PsiElement context, SelectionPath parameters_selector) {
    ObjectType weaveType = loadParametersType(context, parameters_selector);
    implicitInput.addInput(PARAMETERS_KEY, weaveType);
  }

  private void createOperationResponse(ImplicitInput implicitInput, PsiElement context) {
    SelectionPath path = SelectionPath.PARENT.parent().parent().child("base").child("path");
    SelectionPath method = SelectionPath.PARENT.parent().parent().child("base").child("method");
    SelectionPath operationId = SelectionPath.PARENT.parent().parent().child("base").child("operationId");
    final WeaveType payloadType = resolveOperationResponseType(context, path, method)
            .orElse(resolveOperationResponseType(context, operationId)
                    .orElse(new AnyType()));
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(ATTRIBUTES_KEY, createHttpAttributes());
  }

  private void createPayloadWithAttributesInputs(ImplicitInput implicitInput) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType());
    implicitInput.addInput(ATTRIBUTES_KEY, createHttpAttributes());
  }

  private void createPaginationParametersInputs(ImplicitInput implicitInput) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType());
    implicitInput.addInput(LINK_KEY, createEmptyObject());
    implicitInput.addInput(ATTRIBUTES_KEY, createHttpAttributes());
  }

  private void createTriggersWatermarkExtractionBinding(ImplicitInput implicitInput, PsiElement context, SelectionPath parametersSelector) {
    assert parametersSelector.getParent() != null;
    final SelectionPath path = parametersSelector.getParent().child(PATH_KEY);
    final SelectionPath method = parametersSelector.getParent().child(METHOD_KEY);

    final WeaveType payloadType = resolveOperationResponseType(context, path, method).orElse(new AnyType());
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(WATERMARK_KEY, new UnionType(ScalaUtils.toSeq(new NullType(), new DateTimeType())));
    implicitInput.addInput(PARAMETERS_KEY, parametersType);

    final SelectionPath itemsExtractionExpression = parametersSelector.getParent().child("items").child("extraction").child("expression");
    final PsiElement itemsExtractionElement = itemsExtractionExpression.selectYaml(context);
    WeaveType itemType = new AnyType();
    if (itemsExtractionElement instanceof YAMLScalar) {
      final String itemsExtractionTextExpression = ((YAMLScalar) itemsExtractionElement).getTextValue();
      final Project project = context.getProject();
      final String weaveItemsExpression = YamlLanguageInjector.extractWeaveExpression(itemsExtractionTextExpression);
      final ImplicitInput itemImplicitInput = new ImplicitInput();
      itemImplicitInput.addInput(WATERMARK_KEY, new UnionType(ScalaUtils.toSeq(new NullType(), new DateTimeType())));
      itemImplicitInput.addInput(PAYLOAD_KEY, payloadType);
      itemImplicitInput.addInput(PARAMETERS_KEY, parametersType);
      WeaveType weaveType = WeaveToolingService.getInstance(project).typeOf(weaveItemsExpression, itemImplicitInput);
      if (weaveType != null) {
        itemType = arrayTypeOf(weaveType);
      }
    }
    implicitInput.addInput(ITEM_KEY, itemType);
  }

  private WeaveType arrayTypeOf(WeaveType weaveType) {
    if (weaveType instanceof ArrayType) {
      return ((ArrayType) weaveType).of();
    } else if (weaveType instanceof UnionType) {
      WeaveType[] weaveTypes = JavaConverters.asJavaCollection(((UnionType) weaveType).of()).stream().map((wt) -> arrayTypeOf(wt))
              .filter((wt) -> wt != null)
              .toArray(WeaveType[]::new);
      if (weaveTypes.length == 0) {
        return null;
      } else if (weaveTypes.length == 1) {
        return weaveTypes[0];
      } else {
        return new UnionType(ScalaUtils.toSeq(weaveTypes));
      }
    }
    return null;
  }

  private void createSampleDataRequestBinding(ImplicitInput implicitInput, PsiElement context, SelectionPath parametersSelector) {
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
  }


  private void createSampleDataTransformBinding(ImplicitInput implicitInput, PsiElement context, SelectionPath parametersSelector) {
    assert parametersSelector.getParent() != null;
    final SelectionPath path = parametersSelector.getParent().child("definition").child("request").child(PATH_KEY);
    final SelectionPath method = parametersSelector.getParent().child("definition").child("request").child(METHOD_KEY);

    final WeaveType payloadType = resolveOperationResponseType(context, path, method).orElse(new AnyType());
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
  }

  private void createTriggersItemsBinding(ImplicitInput implicitInput, PsiElement context, SelectionPath parametersSelector) {
    assert parametersSelector.getParent() != null;
    final SelectionPath path = parametersSelector.getParent().child(PATH_KEY);
    final SelectionPath method = parametersSelector.getParent().child(METHOD_KEY);

    final WeaveType payloadType = resolveOperationResponseType(context, path, method).orElse(new AnyType());
    final ObjectType parametersType = loadParametersType(context, parametersSelector);
    implicitInput.addInput(PAYLOAD_KEY, payloadType);
    implicitInput.addInput(WATERMARK_KEY, new UnionType(ScalaUtils.toSeq(new NullType(), new DateTimeType())));
    implicitInput.addInput(PARAMETERS_KEY, parametersType);
  }

  @NotNull
  private ObjectType loadParametersType(PsiElement context, SelectionPath parameters_selector) {
    PsiElement parameters = parameters_selector.selectYaml(context);
    Builder<KeyValuePairType, Seq<KeyValuePairType>> objectSeqBuilder = Seq$.MODULE$.newBuilder();
    if (parameters instanceof YAMLMapping) {
      for (YAMLKeyValue keyValue : ((YAMLMapping) parameters).getKeyValues()) {
        WeaveType value = new AnyType();
        if (keyValue.getValue() instanceof YAMLMapping) {
          YAMLMapping parameterMapping = (YAMLMapping) keyValue.getValue();
          YAMLKeyValue type = parameterMapping.getKeyValueByKey("type");
          if (type != null && type.getValue() != null) {
            String text = type.getValue().getText().trim();
            switch (text) {
              case "boolean":
                value = new BooleanType(Option.empty(), VariableConstraints.emptyConstraints());
                break;
              case "integer":
              case "number":
                value = new NumberType(Option.empty());
                break;
              case "localDateTime":
                value = new LocalDateTimeType();
                break;
              case "zonedDateTime":
                value = new DateTimeType();
                break;
              case "string":
                value = new StringType(Option.empty());
                break;
            }
          } else {
            final YAMLKeyValue typeSchema = parameterMapping.getKeyValueByKey("typeSchema");
            if (typeSchema != null && typeSchema.getValue() != null) {
              final String path = typeSchema.getValue().getText().trim();
              final PsiFile restSdkFile = context.getContainingFile().getOriginalFile();
              final VirtualFile parent = restSdkFile.getOriginalFile().getVirtualFile().getParent();
              final VirtualFile child = parent.findFileByRelativePath(path);
              if (child != null) {
                PsiFile jsonSchemaFile = PsiManager.getInstance(context.getProject()).findFile(child);
                if (jsonSchemaFile != null) {
                  value = JsonSchemaConverter.toWeaveType(jsonSchemaFile);
                }
              }
            }
          }
        }

        KeyValuePairType kvp = createKVPair(keyValue.getName(), value);
        objectSeqBuilder.$plus$eq(kvp);
      }
    }
    return new ObjectType(objectSeqBuilder.result(), false, false);
  }

  @NotNull
  private KeyValuePairType createKVPair(String fieldName, WeaveType value) {
    NameType name = new NameType(Some.apply(new QName(fieldName, Option.empty())));
    return new KeyValuePairType(new KeyType(name, Seq$.MODULE$.<NameValuePairType>newBuilder().result()), value, false, false);
  }

  @NotNull
  private ObjectType createHttpAttributes() {
    Builder<KeyValuePairType, Seq<KeyValuePairType>> fieldsBuilder = Seq$.MODULE$.newBuilder();
    fieldsBuilder.$plus$eq(createKVPair("statusCode", new NumberType(Option.empty())));
    fieldsBuilder.$plus$eq(createKVPair("headers", createEmptyObject()));
    fieldsBuilder.$plus$eq(createKVPair("reasonPhrase", new StringType(Option.empty())));
    return new ObjectType(fieldsBuilder.result(), false, false);
  }

  @NotNull
  private ObjectType createEmptyObject() {
    return new ObjectType(Seq$.MODULE$.<KeyValuePairType>newBuilder().result(), false, false);
  }

  @Override
  public @NotNull Optional<WeaveType> expectedOutput(PsiFile psiFile) {
    PsiElement context = psiFile.getContext();
    if (context != null) {
      final SelectionPath path = pathOfYaml(context);
      if (path.matches(RestSdkPaths.OPERATION_IDENTIFIER_PATH) || path.matches(RestSdkPaths.SECURITY_ERROR_TEMPLATE_PATH)) {
        return Optional.of(new StringType(Option.empty()));
      } else if (path.matches(RestSdkPaths.PAGINATION_PATH)) {
        return Optional.of(new ArrayType(new AnyType()));
      } else if (path.matches(RestSdkPaths.SECURITY_REFRESH_PATH) || path.matches(RestSdkPaths.SECURITY_VALIDATION_PATH)) {
        return Optional.of(new BooleanType(Option.empty(), VariableConstraints.emptyConstraints()));
      } else if (path.matches(RestSdkPaths.OPERATION_REQUEST_BODY_PATH)) {
        final PsiElement operationId = RestSdkPaths.RELATIVE_OPERATION_BASE_FROM_BODY_EXPRESSION_PATH.selectYaml(context);
        if (operationId instanceof YAMLScalar) {
          final Document webApiDocument = RestSdkHelper.parseWebApi(context.getContainingFile());
          if (webApiDocument != null) {
            String opIdValue = ((YAMLScalar) operationId).getTextValue();
            Operation operation = RestSdkHelper.operationById((WebApi) webApiDocument.encodes(), opIdValue);
            if (operation != null && operation.request() != null && !operation.request().payloads().isEmpty()) {
              final WeaveType weaveType = RestSdkHelper.toWeaveType(operation.request().payloads().get(0).schema(), webApiDocument);
              return Optional.of(weaveType);
            }
          }
        }
      } else if (path.matches(RestSdkPaths.OPERATION_RESPONSE_BODY_PATH)) {
        PsiElement typeSchema = SelectionPath.PARENT.child("typeSchema").selectYaml(context);
        if (typeSchema != null && !typeSchema.getText().isBlank()) {
          String typeSchemaPath = typeSchema.getText();
          final PsiFile restSdkFile = context.getContainingFile().getOriginalFile();
          final VirtualFile parent = restSdkFile.getOriginalFile().getVirtualFile().getParent();
          final VirtualFile child = parent.findFileByRelativePath(typeSchemaPath);
          if (child != null) {
            PsiFile jsonSchemaFile = PsiManager.getInstance(context.getProject()).findFile(child);
            if (jsonSchemaFile != null) {
              return Optional.of(JsonSchemaConverter.toWeaveType(jsonSchemaFile));
            }
          }
        }
      } else if (path.matches(RestSdkPaths.TRIGGERS_BINDING_BODY_EXPRESSION)) {
        Optional<WeaveType> weaveType = resolveOperationRequestType(context, RestSdkPaths.RELATIVE_TRIGGER_PATH_FROM_BINDING_BODY_PATH, RestSdkPaths.RELATIVE_TRIGGER_METHOD_FROM_BINDING_BODY_PATH);
        if (weaveType.isPresent()) {
          return weaveType;
        }
      }
    }
    //Load from expected output
    return Optional.ofNullable(loadSampleOutput(psiFile));
  }

  private Optional<WeaveType> resolveOperationRequestType(PsiElement context, SelectionPath pathPath, SelectionPath methodPAth) {
    final PsiElement pathElement = pathPath.selectYaml(context);
    final PsiElement method = methodPAth.selectYaml(context);
    if (pathElement instanceof YAMLScalar && method instanceof YAMLScalar) {
      final String pathText = ((YAMLScalar) pathElement).getTextValue();
      final String methodText = ((YAMLScalar) method).getTextValue();
      final Document webApiDocument = RestSdkHelper.parseWebApi(context.getContainingFile());
      if (webApiDocument != null) {
        final Operation operation = RestSdkHelper.operationByMethodPath((WebApi) webApiDocument.encodes(), methodText, pathText);
        if (operation != null && operation.request() != null && !operation.request().payloads().isEmpty()) {
          final WeaveType weaveType = RestSdkHelper.toWeaveType(operation.request().payloads().get(0).schema(), webApiDocument);
          return Optional.of(weaveType);
        }
      }
    }
    return Optional.empty();
  }

  private Optional<WeaveType> resolveOperationResponseType(PsiElement context, SelectionPath pathPath, SelectionPath methodPAth) {
    final PsiElement pathElement = pathPath.selectYaml(context);
    final PsiElement method = methodPAth.selectYaml(context);
    if (pathElement instanceof YAMLScalar && method instanceof YAMLScalar) {
      final String pathText = ((YAMLScalar) pathElement).getTextValue();
      final String methodText = ((YAMLScalar) method).getTextValue();
      final Document webApiDocument = RestSdkHelper.parseWebApi(context.getContainingFile());
      if (webApiDocument != null) {
        final Operation operation = RestSdkHelper.operationByMethodPath((WebApi) webApiDocument.encodes(), methodText, pathText);
        if (operation != null && !operation.responses().isEmpty() && !operation.responses().get(0).payloads().isEmpty()) {
          final WeaveType weaveType = RestSdkHelper.toWeaveType(operation.responses().get(0).payloads().get(0).schema(), webApiDocument);
          return Optional.of(weaveType);
        }
      }
    }
    return Optional.empty();
  }

  private Optional<WeaveType> resolveOperationResponseType(PsiElement context, SelectionPath operationIdPath) {
    final PsiElement operationID = operationIdPath.selectYaml(context);
    if (operationID instanceof YAMLScalar) {
      final String operationIDValue = ((YAMLScalar) operationID).getTextValue();
      final Document webApiDocument = RestSdkHelper.parseWebApi(context.getContainingFile());
      if (webApiDocument != null) {
        final Operation operation = RestSdkHelper.operationById((WebApi) webApiDocument.encodes(), operationIDValue);
        if (operation != null && !operation.responses().isEmpty() && !operation.responses().get(0).payloads().isEmpty()) {
          final WeaveType weaveType = RestSdkHelper.toWeaveType(operation.responses().get(0).payloads().get(0).schema(), webApiDocument);
          return Optional.of(weaveType);
        }
      }
    }
    return Optional.empty();
  }

}
