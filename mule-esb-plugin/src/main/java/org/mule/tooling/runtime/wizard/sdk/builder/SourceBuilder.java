package org.mule.tooling.runtime.wizard.sdk.builder;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.mule.tooling.runtime.wizard.sdk.builder.BuilderUtils.mediaTypeOf;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONFIG_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_EXCEPTION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.NONE_CONNECTION_PROVIDER;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.ON_ERROR;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.ON_SUCCESS;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.ON_TERMINATE;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SOURCE;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SOURCE_CALLBACK;

import org.mule.tooling.runtime.wizard.sdk.ExtensionBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.ParameterBuilder.FieldParameterBuilder;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class SourceBuilder extends TypeBasedBuilder {

    private final ParameterizedTypeName sourceCallbackType;
    private String name;
    private TypeName configType;
    private TypeName connectionType;
    private ExtensionBuilder extensionBuilder;

    private AnnotationSpec mediaType = mediaTypeOf("*/*");

    private MethodSpec.Builder onStartMethod = onStart()
            .addStatement("System.out.println($S)", "starting");

    private MethodSpec.Builder onStopMethod = onStop()
            .addStatement("System.out.println($S)", "stopping");

    private MethodSpec.Builder onSuccessMethod = onSuccess()
            .addStatement("System.out.println($S)", "The message was processed correctly");

    private MethodSpec.Builder onErrorMethod = onError()
            .addStatement("System.out.println($S)", "An error occurred processing the message");

    private MethodSpec.Builder onTerminateMethod = onTerminate()
            .addStatement("System.out.println($S)", "Terminated message processing");

    public SourceBuilder(String name, TypeName payloadType, TypeName attributesTypes, TypeName configType, TypeName connectionType, ExtensionBuilder extensionBuilder) {
        super(TypeSpec.classBuilder(name)
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(SOURCE, payloadType, attributesTypes)));
        this.name = name;
        this.configType = configType;
        this.connectionType = connectionType;
        this.extensionBuilder = extensionBuilder;

        getTypeSpec()
                .addField(FieldSpec
                        .builder(ParameterizedTypeName.get(NONE_CONNECTION_PROVIDER, connectionType), "connection", PRIVATE)
                        .addAnnotation(CONNECTION_ANNOTATION)
                        .build());

        getTypeSpec().addField(FieldSpec
                .builder(configType, "config", PRIVATE)
                .addAnnotation(CONFIG_ANNOTATION)
                .build());


        this.sourceCallbackType = ParameterizedTypeName.get(SOURCE_CALLBACK, payloadType, attributesTypes);
    }

    public TypeName getSourceCallbackType() {
        return sourceCallbackType;
    }

    public TypeName getConnectionType() {
        return connectionType;
    }

    public TypeName getConfigType() {
        return configType;
    }

    public MethodSpec.Builder onStart() {
        onStartMethod = MethodSpec.methodBuilder("onStart")
                .addModifiers(PUBLIC)
                .addException(CONNECTION_EXCEPTION);
        return onStartMethod;
    }

    public MethodSpec.Builder onStop() {
        onStopMethod = MethodSpec.methodBuilder("onStop")
                .addModifiers(PUBLIC);
        return onStopMethod;
    }

    public MethodSpec.Builder onSuccess() {
        onSuccessMethod = methodBuilder("onSuccess")
                .addModifiers(PUBLIC)
                .addAnnotation(ON_SUCCESS);
        return onSuccessMethod;
    }

    public MethodSpec.Builder onError() {
        onErrorMethod = methodBuilder("onError")
                .addModifiers(PUBLIC)
                .addAnnotation(ON_ERROR);
        return onErrorMethod;
    }

    public MethodSpec.Builder onTerminate() {
        onTerminateMethod = methodBuilder("onTerminate")
                .addModifiers(PUBLIC)
                .addAnnotation(ON_TERMINATE);
        return onTerminateMethod;
    }

    @Override
    public String getPackage() {
        return extensionBuilder.getExtensionPackage() + ".internal.source";
    }

    @Override
    public String getName() {
        return name;
    }

    public TypeSpec doBuild() {
        for (FieldParameterBuilder parameter : super.parameters) {
            getTypeSpec().addField(parameter.build());
        }

        getTypeSpec().addMethod(onStartMethod.build());
        getTypeSpec().addMethod(onStopMethod.build());

        if(onSuccessMethod != null) {
            getTypeSpec().addMethod(onSuccessMethod.build());
        }
        if(onErrorMethod != null) {
            getTypeSpec().addMethod(onErrorMethod.build());
        }
        if(onTerminateMethod != null) {
            getTypeSpec().addMethod(onTerminateMethod.build());
        }

        return getTypeSpec().build();
    }
}