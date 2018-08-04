package org.mule.tooling.runtime.wizard.sdk.builder;

import static org.mule.tooling.runtime.wizard.sdk.builder.BuilderUtils.mediaTypeOf;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONFIG_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_ANNOTATION;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;

/**
 * Builder to create extension operations
 */
public class OperationBuilder extends MethodBuilder<OperationBuilder> {
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
        builder.addAnnotation(mediaTypeOf(mediaType));
        return this;
    }
}
