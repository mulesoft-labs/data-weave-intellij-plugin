package org.mule.tooling.restsdk.datasense;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.InputOutputTypesProvider;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;
import org.mule.tooling.restsdk.utils.RestSdkPaths;
import org.mule.tooling.restsdk.utils.YamlPath;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.parser.ast.QName;
import org.mule.weave.v2.ts.*;
import scala.Option;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.Builder;

import java.util.Optional;

import static org.mule.tooling.restsdk.utils.RestSdkHelper.isInRestSdkContextFile;
import static org.mule.tooling.restsdk.utils.YamlPath.pathOf;

public class RestSdkInputOutputTypesProvider implements InputOutputTypesProvider {
  //Bindings
  public static final String PARAMETERS_KEY = "parameters";
  public static final String WATERMARK_KEY = "watermark";
  //    Selectors
  public static final String PAYLOAD_KEY = "payload";
  public static final String ITEM_KEY = "item";
  public static final String ATTRIBUTES_KEY = "attributes";
  public static final String LINK_KEY = "link";
  public static final String OPERATION_ID = "operationId";
  public static final String METHOD = "method";
  public static final String PATH = "path";
  public static final String SUMMARY = "summary";

  @Override
  public boolean support(PsiFile psiFile) {
    return isInRestSdkContextFile(psiFile);
  }

  @Override
  public @NotNull ImplicitInput inputTypes(PsiFile psiFile) {
    ImplicitInput implicitInput = new ImplicitInput();
    PsiElement context = psiFile.getContext();
    if (context != null) {
      YamlPath path = pathOf(context);
      if (path.matches(RestSdkPaths.OPERATION_IDENTIFIER_PATH)) {
        implicitInput.addInput(OPERATION_ID, new StringType(Option.<String>empty()));
        implicitInput.addInput(METHOD, new StringType(Option.<String>empty()));
        implicitInput.addInput(PATH, new StringType(Option.<String>empty()));
      } else if (path.matches(RestSdkPaths.PAGINATION_PATH) || path.matches(RestSdkPaths.SECURITY_VALIDATION_PATH)
              || path.matches(RestSdkPaths.SECURITY_ERROR_TEMPLATE_PATH) || path.matches(RestSdkPaths.SECURITY_REFRESH_PATH)
              || path.matches(RestSdkPaths.TRIGGERS_SAMPLE_DATA_PATH)) {
        createPayloadWithAttributesInputs(implicitInput);
      } else if (path.matches(RestSdkPaths.TRIGGERS_BINDING_VALUE) || path.matches(RestSdkPaths.TRIGGERS_BINDING_BODY_EXPRESSION)) {
        createTriggersBinding(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR);
      } else if (path.matches(RestSdkPaths.TRIGGERS_WATERMARK_PATH) || path.matches(RestSdkPaths.TRIGGERS_ITEMS_PATH)) {
        createTriggersInputs(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_ITEMS);
      } else if (path.matches(RestSdkPaths.SAMPLE_DATA_URI_PARAMETER)) {
        createTriggersInputs(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA);
      } else if (path.matches(RestSdkPaths.OPERATION_VALUE_PROVIDERS)) {
        createValueProvider(implicitInput);
      } else if (path.matches(RestSdkPaths.PAGINATION_PARAMETERS)) {
        createPaginationParametersInputs(implicitInput);
      } else if (path.matches(RestSdkPaths.OPERATION_REQUEST_BODY)) {
        createOperationRequest(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_BODY_REQUEST);
      } else if (path.matches(RestSdkPaths.OPERATION_REQUEST_QUERY_PARAM)) {
        createOperationRequest(implicitInput, context, RestSdkPaths.PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER);
      } else if (path.matches(RestSdkPaths.OPERATION_DISPLAY_NAME_PATH)) {
        implicitInput.addInput(OPERATION_ID, new StringType(Option.empty()));
        implicitInput.addInput(METHOD, new StringType(Option.empty()));
        implicitInput.addInput(PATH, new StringType(Option.empty()));
        implicitInput.addInput(SUMMARY, new StringType(Option.empty()));
      } else if (path.matches(RestSdkPaths.VALUE_PROVIDER_PATH)) {
        createValueProviderInputs(implicitInput, context);
      } else if (path.matches(RestSdkPaths.TEST_CONNECTION_PATH)) {
        createTestConnectionInputs(implicitInput, context);
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

  private void createTriggersBinding(ImplicitInput implicitInput, PsiElement context, YamlPath parameterSelector) {
    ObjectType weaveType = loadParametersType(context, parameterSelector);
    implicitInput.addInput(WATERMARK_KEY, new AnyType()); //TODO shoudln't this be a type of date?
    implicitInput.addInput(PARAMETERS_KEY, weaveType);
  }

  private void createTestConnectionInputs(ImplicitInput implicitInput, PsiElement context) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType()); //TODO can be inferred from the response of the requests being hit after?
    implicitInput.addInput(ATTRIBUTES_KEY, createHttpAttributes());
  }

  private void createValueProviderInputs(ImplicitInput implicitInput, PsiElement context) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType()); //TODO can be inferred from the response of the requests being hit after?
    implicitInput.addInput(ITEM_KEY, new AnyType()); //TODO can be inferred from the response of the requests being hit after applying the `extraction` expression on it?
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


  private void createValueProvider(ImplicitInput implicitInput) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType());
    implicitInput.addInput(ITEM_KEY, new AnyType());
  }

  private void createOperationRequest(ImplicitInput implicitInput, PsiElement context, YamlPath parameters_selector) {
    ObjectType weaveType = loadParametersType(context, parameters_selector);
    implicitInput.addInput(PARAMETERS_KEY, weaveType);
  }

  private void createOperationResponse(ImplicitInput implicitInput) {
    implicitInput.addInput(PAYLOAD_KEY, new AnyType());
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

  private void createTriggersInputs(ImplicitInput implicitInput, PsiElement context, YamlPath parameters_selector) {
    ObjectType weaveType = loadParametersType(context, parameters_selector);
    implicitInput.addInput(PAYLOAD_KEY, new AnyType());
    implicitInput.addInput(WATERMARK_KEY, new AnyType());
    implicitInput.addInput(ITEM_KEY, new AnyType());
    implicitInput.addInput(PARAMETERS_KEY, weaveType);
  }

  @NotNull
  private ObjectType loadParametersType(PsiElement context, YamlPath parameters_selector) {
    PsiElement parameters = parameters_selector.select(context);
    Builder<KeyValuePairType, Seq<KeyValuePairType>> objectSeqBuilder = Seq$.MODULE$.newBuilder();
    if (parameters instanceof YAMLMapping) {
      for (YAMLKeyValue keyValue : ((YAMLMapping) parameters).getKeyValues()) {
        WeaveType value = new AnyType();
        if (keyValue.getValue() instanceof YAMLMapping) {
          YAMLKeyValue type = ((YAMLMapping) keyValue.getValue()).getKeyValueByKey("type");
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
    Builder<KeyValuePairType, Seq<KeyValuePairType>> fieldsBuilder = Seq$.MODULE$.<KeyValuePairType>newBuilder();
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
      YamlPath path = pathOf(context);
      if (path.matches(RestSdkPaths.OPERATION_IDENTIFIER_PATH) || path.matches(RestSdkPaths.SECURITY_ERROR_TEMPLATE_PATH)) {
        return Optional.of(new StringType(Option.empty()));
      } else if (path.matches(RestSdkPaths.PAGINATION_PATH)) {
        return Optional.of(new ArrayType(new AnyType()));
      } else if (path.matches(RestSdkPaths.SECURITY_REFRESH_PATH) || path.matches(RestSdkPaths.SECURITY_VALIDATION_PATH)) {
        return Optional.of(new BooleanType(Option.empty(), VariableConstraints.emptyConstraints()));
      }
    }
    //Load from expected output
    return Optional.ofNullable(loadSampleOutput(psiFile));
  }

}
