package org.mule.tooling.runtime.wizard.sdk.builder;

import static javax.lang.model.element.Modifier.PUBLIC;

import org.mule.tooling.runtime.wizard.sdk.ExtensionBuilder;

import java.util.ArrayList;
import java.util.List;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder to create operation containers
 */
public class OperationContainerBuilder extends TypeBasedBuilder {

    private final TypeSpec.Builder builder;
    private final ClassName configuration;
    private final ClassName connection;
    private final List<OperationBuilder> operations = new ArrayList<>();
    private final String className;
    private ExtensionBuilder extensionBuilder;

    public OperationContainerBuilder(String className, ClassName configuration, ClassName connection, ExtensionBuilder extensionBuilder) {
        super(TypeSpec.classBuilder(className).addModifiers(PUBLIC));
        this.configuration = configuration;
        this.connection = connection;
        this.className = className.replace(" ", "");
        this.extensionBuilder = extensionBuilder;
        builder = getTypeSpec();
    }

    public OperationBuilder withOperation(String name){
        OperationBuilder operationBuilder = new OperationBuilder(name, configuration, connection);
        operations.add(operationBuilder);
        return operationBuilder;
    }

    @Override
    public String getPackage() {
        return extensionBuilder.getExtensionPackage() + ".internal.operations";
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public TypeSpec doBuild() {

        for (OperationBuilder operation : operations) {
            builder.addMethod(operation.build());
        }

        return builder.build();
    }
}