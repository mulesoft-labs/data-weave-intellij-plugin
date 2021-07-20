package org.mule.tooling.restsdk.datasense;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;
import org.mule.tooling.lang.dw.service.InputOutputTypesProvider;
import org.mule.tooling.restsdk.schema.RestSdkDescriptorFileProvider;
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

public class RestSdkInputOutputTypesProvider implements InputOutputTypesProvider {

    public static final Path OPERATION_IDENTIFIER_PATH = Path.DOCUMENT.child("operationIdentifier").child("expression");
    public static final Path PAGINATION_PATH = Path.DOCUMENT.child("paginations").child("*").child("pagingResponse").child("expression");
    public static final Path VALIDATION_PATH = Path.DOCUMENT.child("security").child("*").child("responseValidation").arrayItem().child("validation").child("expression");
    public static final Path ERROR_TEMPLATE_PATH = Path.DOCUMENT.child("security").child("*").child("responseValidation").arrayItem().child("validation").child("errorTemplate");
    public static final Path REFRESH_PATH = Path.DOCUMENT.child("security").child("*").child("refreshTokenCondition");
    public static final Path TRIGGERS_PATH = Path.DOCUMENT.child("triggers").child("*").child("binding").any().any().child("value");
    public static final Path TRIGGERS_WATERMARK_PATH = Path.DOCUMENT.child("triggers").child("*").child("watermark").child("extraction").child("expression");
    public static final Path TRIGGERS_ITEMS_PATH = Path.DOCUMENT.child("triggers").child("*").child("items").child("extraction").child("expression");
    public static final Path TRIGGERS_SAMPLE_DATA_PATH = Path.DOCUMENT.child("triggers").child("*").child("sampleData").child("transform").child("expression");
    public static final Path SAMPLE_DATA_URI_PARAMETER = Path.DOCUMENT.child("sampleData").any().child("definition").child("request").child("binding").any().any().child("value");

    public static final Path PARAMETERS_SELECTOR = Path.PARENT.parent().parent().parent().child("parameters");
    public static final Path PARAMETERS_SELECTOR_FROM_ITEMS = Path.PARENT.parent().parent().child("parameters");
    public static final Path PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA = Path.PARENT.parent().parent().parent().parent().parent().child("parameters");

    @Override
    public boolean support(PsiFile psiFile) {
        PsiElement context = psiFile.getContext();
        if (context instanceof YAMLQuotedText) {
            String text = context.getContainingFile().getText();
            return RestSdkDescriptorFileProvider.isRestSdkDescriptor(text);
        } else {
            return false;
        }
    }

    @Override
    public ImplicitInput inputTypes(PsiFile psiFile) {
        ImplicitInput implicitInput = new ImplicitInput();
        PsiElement context = psiFile.getContext();
        if (context != null) {
            Path path = pathOf(context);
            if (path.matches(OPERATION_IDENTIFIER_PATH)) {
                implicitInput.addInput("operationId", new StringType(Option.empty()));
                implicitInput.addInput("method", new StringType(Option.empty()));
                implicitInput.addInput("path", new StringType(Option.empty()));
            } else if (path.matches(PAGINATION_PATH) || path.matches(VALIDATION_PATH) || path.matches(ERROR_TEMPLATE_PATH) || path.matches(REFRESH_PATH) || path.matches(TRIGGERS_SAMPLE_DATA_PATH)) {
                createPayloadWithAttributesInputs(implicitInput);
            } else if (path.matches(TRIGGERS_PATH)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR);
            } else if (path.matches(TRIGGERS_WATERMARK_PATH) || path.matches(TRIGGERS_ITEMS_PATH)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR_FROM_ITEMS);
            } else if (path.matches(SAMPLE_DATA_URI_PARAMETER)) {
                createTriggersInputs(implicitInput, context, PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA);
            }
        }
        return implicitInput;
    }

    private void createPayloadWithAttributesInputs(ImplicitInput implicitInput) {
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("attributes", createHttpAttributes());
    }

    private void createTriggersInputs(ImplicitInput implicitInput, PsiElement context, Path parameters_selector) {
        ObjectType weaveType = loadParametersType(context, parameters_selector);
        implicitInput.addInput("payload", new AnyType());
        implicitInput.addInput("watermark", new AnyType());
        implicitInput.addInput("items", new AnyType());
        implicitInput.addInput("parameters", weaveType);
    }

    @NotNull
    private ObjectType loadParametersType(PsiElement context, Path parameters_selector) {
        PsiElement parameters = parameters_selector.select(context);
        Builder<KeyValuePairType, Seq<KeyValuePairType>> objectSeqBuilder = Seq$.MODULE$.newBuilder();
        if (parameters instanceof YAMLMapping) {
            Iterator<YAMLKeyValue> paramsKV = ((YAMLMapping) parameters).getKeyValues().iterator();
            while (paramsKV.hasNext()) {
                YAMLKeyValue next = paramsKV.next();
                NameType name = new NameType(Some.apply(new QName(next.getName(), Option.empty())));
                //TODO load the type correctly
                StringType value = new StringType(Option.empty());
                KeyValuePairType kvp = new KeyValuePairType(new KeyType(name, Seq$.MODULE$.<NameValuePairType>newBuilder().result()), value, false, false);
                objectSeqBuilder.$plus$eq(kvp);
            }
        }
        ObjectType weaveType = new ObjectType(objectSeqBuilder.result(), false, false);
        return weaveType;
    }


    @NotNull
    private ObjectType createHttpAttributes() {
        return new ObjectType(Seq$.MODULE$.<KeyValuePairType>newBuilder().result(), false, false);
    }

    public static Path pathOf(PsiElement element) {
        if (element instanceof YAMLDocument) {
            return Path.DOCUMENT;
        } else if (element instanceof YAMLKeyValue) {
            return pathOf(element.getParent()).child(((YAMLKeyValue) element).getKeyText());
        } else if (element instanceof YAMLSequenceItem) {
            return pathOf(element.getParent()).arrayItem();
        } else {
            return pathOf(element.getParent());
        }
    }

    @Override
    public Optional<WeaveType> expectedOutput(PsiFile psiFile) {
        PsiElement context = psiFile.getContext();
        if (context != null) {
            Path path = pathOf(context);
            if (path.matches(OPERATION_IDENTIFIER_PATH) || path.matches(ERROR_TEMPLATE_PATH)) {
                return Optional.of(new StringType(Option.empty()));
            } else if (path.matches(PAGINATION_PATH)) {
                return Optional.of(new ArrayType(new AnyType()));
            } else if (path.matches(REFRESH_PATH) || path.matches(VALIDATION_PATH)) {
                return Optional.of(new BooleanType(Option.empty(), VariableConstraints.emptyConstraints()));
            }
        }
        return Optional.empty();
    }

    public static class Path {

        public static Path DOCUMENT = new Path(null, "#");
        public static Path PARENT = new Path(null, "..");
        @Nullable
        private Path parent;
        private String name;

        public Path(@Nullable Path parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public boolean matches(Path path) {
            boolean matches;
            if (path.parent != null && parent != null) {
                matches = parent.matches(path.parent);
            } else {
                matches = path.parent == null && parent == null;
            }
            if (matches) {
                matches = path.name.equals(name) || path.name.equals("*");
            }
            return matches;
        }

        @Nullable
        public PsiElement select(@NotNull PsiElement element) {
            PsiElement parentNode = element;
            if (parent != null) {
                parentNode = parent.select(element);
            }
            PsiElement match = null;
            if (name.equals("..")) {
                match = PsiTreeUtil.getParentOfType(parentNode, YAMLMapping.class);
            } else if (name.equals("[]")) {
                if (parentNode instanceof YAMLSequence) {
                    Iterator<YAMLSequenceItem> iterator = ((YAMLSequence) parentNode).getItems().iterator();
                    if (iterator.hasNext()) {
                        match = iterator.next().getValue();
                    }
                }
            } else if (name.equals("*")) {
                if (parentNode instanceof YAMLMapping) {
                    Iterator<YAMLKeyValue> keyValues = ((YAMLMapping) parentNode).getKeyValues().iterator();
                    if (keyValues.hasNext()) {
                        match = keyValues.next().getValue();
                    }
                }
            } else if (parentNode instanceof YAMLMapping) {
                YAMLKeyValue keyValueByKey = ((YAMLMapping) parentNode).getKeyValueByKey(name);
                if (keyValueByKey != null) {
                    match = keyValueByKey.getValue();
                }
            }

            return match;

        }

        public Path arrayItem() {
            return new Path(this, "[]");
        }

        public Path child(String name) {
            return new Path(this, name);
        }

        public Path any() {
            return new Path(this, "*");
        }

        public Path parent() {
            return new Path(this, "..");
        }

        public String toString() {
            if (parent != null) {
                return parent.toString() + "/" + name;
            } else {
                return name;
            }
        }
    }
}
