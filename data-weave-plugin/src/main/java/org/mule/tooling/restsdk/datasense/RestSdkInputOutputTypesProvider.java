package org.mule.tooling.restsdk.datasense;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.mule.tooling.lang.dw.service.InputOutputTypesProvider;
import org.mule.tooling.restsdk.utils.YamlPath;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.parser.ast.QName;
import org.mule.weave.v2.ts.*;
import scala.Option;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.Builder;

import java.util.Iterator;
import java.util.Optional;

import static org.mule.tooling.restsdk.utils.RestSdkHelper.isRestSdkFile;
import static org.mule.tooling.restsdk.utils.YamlPath.pathOf;

public class RestSdkInputOutputTypesProvider implements InputOutputTypesProvider {

    public static final YamlPath OPERATION_IDENTIFIER_PATH = YamlPath.DOCUMENT.child("operationIdentifier").child("expression");

    public static final YamlPath PAGINATION_PATH = YamlPath.DOCUMENT.child("paginations").any().child("pagingResponse").child("expression");
    public static final YamlPath PAGINATION_PARAMETERS = YamlPath.DOCUMENT.child("paginations").any().child("parameters").any().child("expression");

    public static final YamlPath SECURITY_VALIDATION_PATH = YamlPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("expression");
    public static final YamlPath SECURITY_ERROR_TEMPLATE_PATH = YamlPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("errorTemplate");
    public static final YamlPath SECURITY_REFRESH_PATH = YamlPath.DOCUMENT.child("security").any().child("refreshTokenCondition");

    public static final YamlPath TRIGGERS_BINDING_VALUE = YamlPath.DOCUMENT.child("triggers").any().child("binding").any().any().child("value");
    public static final YamlPath TRIGGERS_BINDING_BODY_EXPRESSION = YamlPath.DOCUMENT.child("triggers").any().child("binding").any().child("expression");
    public static final YamlPath TRIGGERS_WATERMARK_PATH = YamlPath.DOCUMENT.child("triggers").any().child("watermark").child("extraction").child("expression");
    public static final YamlPath TRIGGERS_ITEMS_PATH = YamlPath.DOCUMENT.child("triggers").any().child("items").child("extraction").child("expression");
    public static final YamlPath TRIGGERS_SAMPLE_DATA_PATH = YamlPath.DOCUMENT.child("triggers").any().child("sampleData").child("transform").child("expression");

    public static final YamlPath SAMPLE_DATA_URI_PARAMETER = YamlPath.DOCUMENT.child("sampleData").any().child("definition").child("request").child("binding").any().any().child("value");

    public static final YamlPath OPERATION_VALUE_PROVIDERS = YamlPath.DOCUMENT.child("endpoints").any().child("operations").any().child("expects").child("body").any().any().child("valueProvider").child("items").any().any().child("expression");
    public static final YamlPath OPERATION_REQUEST_BODY = YamlPath.DOCUMENT.child("operations").any().child("request").child("body").child("expression");
    public static final YamlPath OPERATION_REQUEST_QUERY_PARAM = YamlPath.DOCUMENT.child("operations").any().child("request").any().any().child("value");

    //    Selectors

    public static final YamlPath PARAMETERS_SELECTOR_FROM_BODY_REQUEST = YamlPath.PARENT.parent().parent().child("parameters");
    public static final YamlPath PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER = YamlPath.PARENT.parent().parent().parent().child("parameters");

    public static final YamlPath PARAMETERS_SELECTOR = YamlPath.PARENT.parent().parent().parent().child("parameters");
    public static final YamlPath PARAMETERS_SELECTOR_FROM_ITEMS = YamlPath.PARENT.parent().parent().child("parameters");
    public static final YamlPath PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA = YamlPath.PARENT.parent().parent().parent().parent().parent().child("parameters");

    @Override
    public boolean support(PsiFile psiFile) {
        return isRestSdkFile(psiFile);
    }

    @Override
    public ImplicitInput inputTypes(PsiFile psiFile) {
        ImplicitInput implicitInput = new ImplicitInput();
        PsiElement context = psiFile.getContext();
        if (context != null) {
            YamlPath path = pathOf(context);
            if (path.matches(OPERATION_IDENTIFIER_PATH)) {
                implicitInput.addInput("operationId", new StringType(Option.empty()));
                implicitInput.addInput("method", new StringType(Option.empty()));
                implicitInput.addInput("path", new StringType(Option.empty()));
            } else if (path.matches(PAGINATION_PATH) || path.matches(SECURITY_VALIDATION_PATH)
                    || path.matches(SECURITY_ERROR_TEMPLATE_PATH) || path.matches(SECURITY_REFRESH_PATH)
                    || path.matches(TRIGGERS_SAMPLE_DATA_PATH)) {
                createPayloadWithAttributesInputs(implicitInput);
            } else if (path.matches(TRIGGERS_BINDING_VALUE)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR);
            } else if (path.matches(TRIGGERS_WATERMARK_PATH) || path.matches(TRIGGERS_ITEMS_PATH) || path.matches(TRIGGERS_BINDING_BODY_EXPRESSION)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR_FROM_ITEMS);
            } else if (path.matches(SAMPLE_DATA_URI_PARAMETER)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA);
            } else if (path.matches(OPERATION_VALUE_PROVIDERS)) {
                createValueProvider(implicitInput);
            } else if (path.matches(PAGINATION_PARAMETERS)) {
                createPaginationParametersInputs(implicitInput);
            } else if (path.matches(OPERATION_REQUEST_BODY)) {
                createOperationRequest(implicitInput, context, PARAMETERS_SELECTOR_FROM_BODY_REQUEST);
            } else if (path.matches(OPERATION_REQUEST_QUERY_PARAM)) {
                createOperationRequest(implicitInput, context, PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER);
            } else {
                createPayloadWithAttributesInputs(implicitInput);
            }
        }
        return implicitInput;
    }

    private void createValueProvider(ImplicitInput implicitInput) {
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("item", new AnyType());
    }

    private void createOperationRequest(ImplicitInput implicitInput, PsiElement context, YamlPath parameters_selector) {
        ObjectType weaveType = loadParametersType(context, parameters_selector);
        implicitInput.addInput("parameters", weaveType);
    }

    private void createOperationResponse(ImplicitInput implicitInput) {
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("attributes", createHttpAttributes());
    }

    private void createPayloadWithAttributesInputs(ImplicitInput implicitInput) {
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("attributes", createHttpAttributes());
    }

    private void createPaginationParametersInputs(ImplicitInput implicitInput) {
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("link", createEmptyObject());
        implicitInput.addInput("attributes", createHttpAttributes());
    }

    private void createTriggersInputs(ImplicitInput implicitInput, PsiElement context, YamlPath parameters_selector) {
        ObjectType weaveType = loadParametersType(context, parameters_selector);
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("watermark", new AnyType());
        implicitInput.addInput("item", new AnyType());
        implicitInput.addInput("parameters", weaveType);
    }

    @NotNull
    private ObjectType loadParametersType(PsiElement context, YamlPath parameters_selector) {
        PsiElement parameters = parameters_selector.select(context);
        Builder<KeyValuePairType, Seq<KeyValuePairType>> objectSeqBuilder = Seq$.MODULE$.newBuilder();
        if (parameters instanceof YAMLMapping) {
            Iterator<YAMLKeyValue> paramsKV = ((YAMLMapping) parameters).getKeyValues().iterator();
            while (paramsKV.hasNext()) {
                YAMLKeyValue keyValue = paramsKV.next();
                WeaveType value = new AnyType();
                if (keyValue.getValue() instanceof YAMLMapping) {
                    YAMLKeyValue type = ((YAMLMapping) keyValue.getValue()).getKeyValueByKey("type");
                    if (type != null && type.getValue() != null) {
                        String text = type.getValue().getText().trim();
                        if (text.equals("boolean")) {
                            value = new BooleanType(Option.empty(), VariableConstraints.emptyConstraints());
                        } else if (text.equals("integer") || text.equals("number")) {
                            value = new NumberType(Option.empty());
                        } else if (text.equals("localDateTime")) {
                            value = new LocalDateTimeType();
                        } else if (text.equals("zonedDateTime")) {
                            value = new DateTimeType();
                        } else if (text.equals("string")) {
                            value = new StringType(Option.empty());
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
    public Optional<WeaveType> expectedOutput(PsiFile psiFile) {
        PsiElement context = psiFile.getContext();
        if (context != null) {
            YamlPath path = pathOf(context);
            if (path.matches(OPERATION_IDENTIFIER_PATH) || path.matches(SECURITY_ERROR_TEMPLATE_PATH)) {
                return Optional.of(new StringType(Option.empty()));
            } else if (path.matches(PAGINATION_PATH)) {
                return Optional.of(new ArrayType(new AnyType()));
            } else if (path.matches(SECURITY_REFRESH_PATH) || path.matches(SECURITY_VALIDATION_PATH)) {
                return Optional.of(new BooleanType(Option.empty(), VariableConstraints.emptyConstraints()));
            }
        }
        return Optional.empty();
    }

}
