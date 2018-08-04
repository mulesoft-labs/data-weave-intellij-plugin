package org.mule.tooling.runtime.wizard.sdk.builder;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CACHED_CONNECTION_PROVIDER;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_EXCEPTION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_VALIDATION_RESULT;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.NONE_CONNECTION_PROVIDER;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.POOLED_CONNECTION_PROVIDER;

import org.mule.tooling.runtime.wizard.sdk.ExtensionBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.ParameterBuilder.FieldParameterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

public class ConnectionProviderBuilder extends TypeBasedBuilder {

    private final TypeSpec.Builder connectionProviderClass;

    private ExtensionBuilder extensionBuilder;
    private Consumer<TypeSpec.Builder> connectionProviderInterfaceConfigurator;
    private String name;
    private ClassName connectionType;
    private List<MethodBuilder> methods = new ArrayList<>();
    private MethodSpec connectMethod;

    public ConnectionProviderBuilder(String name, ClassName connectionType, ExtensionBuilder extensionBuilder) {
        super(TypeSpec
                .classBuilder(name)
                .addModifiers(PUBLIC));
        this.name = name;
        this.connectionType = connectionType;
        this.connectionProviderClass = getTypeSpec();
        this.extensionBuilder = extensionBuilder;
        asCached();

        connectMethod = methodBuilder("connect")
                .addModifiers(PUBLIC)
                .addException(CONNECTION_EXCEPTION)
                .returns(connectionType)
                .addStatement("return new $T()", connectionType).build();

    }

    public ConnectionProviderBuilder withConnectMethod(CodeBlock connectMethod){
        this.connectMethod = methodBuilder("connect")
                .addModifiers(PUBLIC)
                .addException(CONNECTION_EXCEPTION)
                .returns(connectionType)
                .addCode(connectMethod)
                .build();
        return this;
    }

    public ConnectionProviderBuilder asCached() {
        connectionProviderInterfaceConfigurator = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(CACHED_CONNECTION_PROVIDER, connectionType));
        return this;
    }

    public ConnectionProviderBuilder asPooled() {
        connectionProviderInterfaceConfigurator = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(POOLED_CONNECTION_PROVIDER, connectionType));
        return this;
    }

    public ConnectionProviderBuilder asNone() {
        connectionProviderInterfaceConfigurator = (builder) -> builder.addSuperinterface(ParameterizedTypeName.get(NONE_CONNECTION_PROVIDER, connectionType));
        return this;
    }

    public MethodBuilder withMethod(String name) {
        MethodBuilder methodBuilder = new MethodBuilder(name);
        methods.add(methodBuilder);
        return methodBuilder;
    }

    @Override
    public TypeSpec doBuild() {
        MethodSpec.Builder disconnect = methodBuilder("disconnect")
                .addModifiers(PUBLIC)
                .addParameter(connectionType, "connection")
                .returns(void.class)
                .addComment("Execute disconnection logic");

        MethodSpec.Builder validate = methodBuilder("validate")
                .addModifiers(PUBLIC)
                .addParameter(connectionType, "connection")
                .returns(CONNECTION_VALIDATION_RESULT)
                .addComment("Execute validation logic")
                .addStatement("return $T.success()", CONNECTION_VALIDATION_RESULT);

        connectionProviderClass.addMethod(connectMethod);
        connectionProviderClass.addMethod(disconnect.build());
        connectionProviderClass.addMethod(validate.build());

        for (FieldParameterBuilder parameter : parameters) {
            connectionProviderClass.addField(parameter.build());
        }


        for (MethodBuilder method : methods) {
            connectionProviderClass.addMethod(method.build());
        }

        connectionProviderInterfaceConfigurator.accept(connectionProviderClass);
        return connectionProviderClass.build();
    }

    public void withJavadoc(CodeBlock codeBlock) {
        connectionProviderClass.addJavadoc(codeBlock);
    }

    @Override
    public String getPackage() {
        return extensionBuilder.getExtensionPackage() + ".internal.connection";
    }

    @Override
    public String getName() {
        return name;
    }
}