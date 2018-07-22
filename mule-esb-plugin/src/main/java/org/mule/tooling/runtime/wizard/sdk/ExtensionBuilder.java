package org.mule.tooling.runtime.wizard.sdk;

import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class ExtensionBuilder {

    private static final String ANNOTATIONS_PACKAGE = "org.mule.runtime.extension.api.annotation";
    private static final String CONNECTION_PACKAGE = "org.mule.runtime.api.connection";

    private static final ClassName extensionAnnotation = ClassName.get(ANNOTATIONS_PACKAGE, "Extension");
    private static final ClassName OPERATIONS_ANNOTATION = ClassName.get(ANNOTATIONS_PACKAGE, "Operations");
    private static final ClassName CATEGORY_CLASS = ClassName.get("org.mule.runtime.api.meta", "Category");
    private static final ClassName CONNECTION_PROVIDERS_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.connectivity", "ConnectionProviders");
    private static final ClassName CACHED_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "CachedConnectionProvider");
    private static final ClassName POOLED_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "PooledConnectionProvider");
    private static final ClassName NONE_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "ConnectionProvider");
    private static final ClassName CONNECTION_EXCEPTION = ClassName.get(CONNECTION_PACKAGE, "ConnectionException");
    private static final ClassName CONNECTION_VALIDATION_RESULT = ClassName.get(CONNECTION_PACKAGE, "ConnectionValidationResult");
    private static final ClassName PARAMETER_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Parameter");
    private static final ClassName CONNECTION_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Connection");
    private static final ClassName CONFIG_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Config");

    private String extensionPackage = "org.mule.connectors";

    private final TypeSpec.Builder extensionClassBuilder;
    private final List<ConnectionProviderBuilder> connectionProviders = new ArrayList<>();
    private final List<ParameterBuilder> parameters = new ArrayList<>();
    private OperationContainerBuilder operationContainerBuilder;
    private String extensionName;
    private String sanitizedExtensionName;
    private ClassName connectionType;

    public ExtensionBuilder(String extensionName, String vendorName, Category category) {
        this(extensionName, vendorName, category, "org.mule.connectors");
    }

    public ExtensionBuilder(String extensionName, String vendorName, Category category, String extensionPackage) {
        this.extensionPackage = extensionPackage;
        this.extensionName = extensionName;
        this.sanitizedExtensionName = extensionName.replace(" ", "");
        AnnotationSpec.Builder extensionAnnotationSpec = AnnotationSpec
                .builder(extensionAnnotation)
                .addMember("name", stringOf(this.extensionName) );

        if(vendorName != null) {
            extensionAnnotationSpec.addMember("vendor", stringOf(vendorName));
        }

        if(category != null) {
            extensionAnnotationSpec.addMember("category", CodeBlock.of("$T." + category, CATEGORY_CLASS));
        }

        this.extensionClassBuilder = TypeSpec
                .classBuilder(sanitizedExtensionName + "Connector")
                .addModifiers(PUBLIC)
                .addAnnotation(extensionAnnotationSpec.build());
    }


    public String getName() {
        return extensionName.replace(" ", "") + "Connector";
    }

    public String getPackage() {
        return extensionPackage + ".internal";
    }

    public TypeSpec build() {
        AnnotationSpec.Builder connectionProvidersAnnotationBuilder = AnnotationSpec
                .builder(CONNECTION_PROVIDERS_ANNOTATION);
        for (ConnectionProviderBuilder providerBuilder : connectionProviders) {
            connectionProvidersAnnotationBuilder.addMember("value", CodeBlock.of("$T.class", ClassName.get(providerBuilder.getPackage(), providerBuilder.getName())));
        }

        AnnotationSpec.Builder value = AnnotationSpec.builder(OPERATIONS_ANNOTATION).addMember("value", CodeBlock.of("$T.class", ClassName.get(operationContainerBuilder.getPackage(), operationContainerBuilder.getName())));

        extensionClassBuilder.addAnnotation(value.build());

        extensionClassBuilder.addAnnotation(connectionProvidersAnnotationBuilder
                .build());

        for (ParameterBuilder parameter : parameters) {
            extensionClassBuilder.addField(parameter.build());
        }

        return extensionClassBuilder.build();
    }

    public ParameterBuilder withParameter(String name, ClassName type) {
        ParameterBuilder parameterBuilder = new ParameterBuilder(name, type);
        parameters.add(parameterBuilder);
        return parameterBuilder;
    }

    public ConnectionProviderBuilder withConnectionProvider(String name, ClassName connectionType) {
        this.connectionType = connectionType;
        ConnectionProviderBuilder connectionProviderBuilder = new ConnectionProviderBuilder(name, connectionType);
        connectionProviders.add(connectionProviderBuilder);
        return connectionProviderBuilder;
    }

    public OperationContainerBuilder withOperationContainer() {
        this.operationContainerBuilder = new OperationContainerBuilder(sanitizedExtensionName + "Operations", ClassName.get("org.mule.extensions", extensionName), connectionType);
        return operationContainerBuilder;
    }

    public class ConnectionProviderBuilder {

        private final TypeSpec.Builder connectionProviderClass;
        private ClassName connectionType;
        private String name;
        private Consumer<TypeSpec.Builder> connectionProviderInterfaceConfigurer;
        private List<ParameterBuilder> parameters = new ArrayList<>();
        private List<MethodBuilder> methods = new ArrayList<>();
        private MethodSpec connectMethod;

        public ConnectionProviderBuilder(String name, ClassName connectionType) {
            this.name = name;
            this.connectionType = connectionType;
            this.connectionProviderClass = TypeSpec
                    .classBuilder(name)
                    .addModifiers(PUBLIC);
            asCached();

            connectMethod = MethodSpec.methodBuilder("connect")
                    .addModifiers(PUBLIC)
                    .addException(CONNECTION_EXCEPTION)
                    .returns(connectionType)
                    .addStatement("return new $T()", connectionType).build();

        }

        public ConnectionProviderBuilder withConnectMethod(CodeBlock connectMethod){
            this.connectMethod = MethodSpec.methodBuilder("connect")
                    .addModifiers(PUBLIC)
                    .addException(CONNECTION_EXCEPTION)
                    .returns(connectionType)
                    .addCode(connectMethod)
                    .build();
            return this;
        }

        public ParameterBuilder withParameter(String name, ClassName type) {
            ParameterBuilder parameterBuilder = new ParameterBuilder(name, type);
            parameters.add(parameterBuilder);
            return parameterBuilder;
        }

        public ConnectionProviderBuilder asCached() {
            connectionProviderInterfaceConfigurer = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(CACHED_CONNECTION_PROVIDER, connectionType));
            return this;
        }

        public ConnectionProviderBuilder asPooled() {
            connectionProviderInterfaceConfigurer = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(POOLED_CONNECTION_PROVIDER, connectionType));
            return this;
        }

        public ConnectionProviderBuilder asNone() {
            connectionProviderInterfaceConfigurer = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(NONE_CONNECTION_PROVIDER, connectionType));
            return this;
        }

        public MethodBuilder withMethod(String name) {
            MethodBuilder methodBuilder = new MethodBuilder(name);
            methods.add(methodBuilder);
            return methodBuilder;
        }

        public String getPackage() {
            return extensionPackage + ".internal.connection";
        }

        public String getName() {
            return name;
        }

        public TypeSpec build() {
            MethodSpec.Builder disconnect = MethodSpec.methodBuilder("disconnect")
                    .addModifiers(PUBLIC)
                    .addParameter(connectionType, "connection")
                    .returns(void.class)
                    .addComment("Execute disconnection logic");

            MethodSpec.Builder validate = MethodSpec.methodBuilder("validate")
                    .addModifiers(PUBLIC)
                    .addParameter(connectionType, "connection")
                    .returns(CONNECTION_VALIDATION_RESULT)
                    .addComment("Execute validation logic")
                    .addStatement("return $T.success()", CONNECTION_VALIDATION_RESULT);

            connectionProviderClass.addMethod(connectMethod);
            connectionProviderClass.addMethod(disconnect.build());
            connectionProviderClass.addMethod(validate.build());

            for (ParameterBuilder parameter : parameters) {
                connectionProviderClass.addField(parameter.build());
            }


            for (MethodBuilder method : methods) {
                connectionProviderClass.addMethod(method.build());
            }

            connectionProviderInterfaceConfigurer.accept(connectionProviderClass);
            return connectionProviderClass.build();
        }

        public void withJavadoc(CodeBlock codeBlock) {
            connectionProviderClass.addJavadoc(codeBlock);
        }
    }

    /**
     * Builder to create parameters
     */
    public class ParameterBuilder {
        private final String name;
        private final ClassName type;
        private FieldSpec.Builder builder;

        public ParameterBuilder(String name, ClassName type) {
            this.name = name;
            this.type = type;
            builder = FieldSpec.builder(type, name, Modifier.PRIVATE).addAnnotation(AnnotationSpec.builder(PARAMETER_ANNOTATION).build());
        }

        public ParameterBuilder asOptional() {
            builder.addAnnotation(ClassName.get("org.mule.runtime.extension.api.annotation.param", "Optional"));
            return this;
        }

        public FieldSpec build() {
            return builder.build();
        }
    }

    /**
     * Builder to create operation containers
     */
    public class OperationContainerBuilder {


        private final TypeSpec.Builder builder;
        private final ClassName configuration;
        private final ClassName connection;
        private final List<OperationBuilder> operations = new ArrayList<>();
        private final String className;

        public OperationContainerBuilder(String className, ClassName configuration, ClassName connection) {
            this.configuration = configuration;
            this.connection = connection;
            this.className = className.replace(" ", "");
            builder = TypeSpec.classBuilder(this.className).addModifiers(PUBLIC);
        }

        public String getPackage() {
            return extensionPackage + ".internal.operations";
        }

        public String getName() {
            return className;
        }

        public OperationBuilder withOperation(String name){
            OperationBuilder operationBuilder = new OperationBuilder(name, configuration, connection);
            operations.add(operationBuilder);
            return operationBuilder;
        }

        public TypeSpec build() {

            for (OperationBuilder operation : operations) {
                builder.addMethod(operation.build());
            }


            return builder.build();
        }
    }

    /**
     * Builder to create extension operations
     */
    public class OperationBuilder extends MethodBuilder<OperationBuilder>{
        private final ClassName configuration;
        private final ClassName connection;

        public OperationBuilder(String name, ClassName configuration, ClassName connection) {
            super(name);
            this.configuration = configuration;
            this.connection = connection;
        }

        public OperationBuilder withConfigParameter(String name) {
            builder.addParameter(ParameterSpec
                    .builder(configuration, name)
                    .addAnnotation(CONFIG_ANNOTATION)
                    .build());
            return this;
        }

        public OperationBuilder withConnection(String name) {
            builder.addParameter(ParameterSpec
                    .builder(connection, name)
                    .addAnnotation(CONNECTION_ANNOTATION)
                    .build());
            return this;
        }

        public OperationBuilder withMediaType(String mediaType) {
            builder.addAnnotation(AnnotationSpec
                    .builder(ClassName.get("org.mule.runtime.extension.api.annotation.param", "MediaType"))
                    .addMember("value", CodeBlock.builder().add("\"$L\"", mediaType).build())
                    .build());
            return this;
        }
    }

    /**
     * Builder to write generic methods
     */
    public class MethodBuilder<T extends MethodBuilder> {

        final String name;
        final MethodSpec.Builder builder;

        public MethodBuilder(String name) {
            this.name = name;
            builder = MethodSpec.methodBuilder(name).addModifiers(PUBLIC);
        }

        public T returns(TypeName type) {
            builder.returns(type);
            return (T) this;
        }

        public T withParameter(String name, ClassName type) {
            builder.addParameter(type, name);
            return (T) this;
        }

        public T withStatement(CodeBlock codeBlock) {
            builder.addStatement(codeBlock);
            return (T) this;
        }

        public MethodSpec build() {
            return builder.build();
        }
    }

    private static String stringOf(String vendorName) {
        return "\"" + vendorName + "\"";
    }
}
