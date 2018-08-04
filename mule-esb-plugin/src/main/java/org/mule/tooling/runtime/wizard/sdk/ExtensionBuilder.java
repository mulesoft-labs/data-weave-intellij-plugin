package org.mule.tooling.runtime.wizard.sdk;

import static javax.lang.model.element.Modifier.PUBLIC;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CATEGORY_CLASS;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.CONNECTION_PROVIDERS_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.OPERATIONS_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SOURCES_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.EXTENSION_ANNOTATION;

import org.mule.tooling.runtime.wizard.sdk.builder.ConnectionProviderBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.JavaType;
import org.mule.tooling.runtime.wizard.sdk.builder.OperationContainerBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.ParameterBuilder.FieldParameterBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.SourceBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.TypeBasedBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class ExtensionBuilder extends TypeBasedBuilder {

    private String extensionPackage;

    private final TypeSpec.Builder extensionClassBuilder;
    private final List<ConnectionProviderBuilder> connectionProviders = new ArrayList<>();
    private final List<JavaType> additionalClasses = new ArrayList<>();
    private OperationContainerBuilder operationContainerBuilder;
    private String extensionName;
    private String sanitizedExtensionName;
    private ClassName connectionType;
    private SourceBuilder sourceBuilder;
    private String extensionClassName;
    private ClassName extensionType;

    public ExtensionBuilder(String extensionName, String vendorName, Category category) {
        this(extensionName, vendorName, category, "org.mule.connectors");
    }

    public ExtensionBuilder(String extensionName, String vendorName, Category category, String extensionPackage){
        super(TypeSpec.classBuilder(extensionName.replace(" ", "") + "Connector"));
        this.extensionPackage = extensionPackage;
        this.extensionName = extensionName;
        this.sanitizedExtensionName = extensionName.replace(" ", "");
        AnnotationSpec.Builder extensionAnnotationSpec = AnnotationSpec
                .builder(EXTENSION_ANNOTATION)
                .addMember("name", "$S", this.extensionName );

        if(vendorName != null) {
            extensionAnnotationSpec.addMember("vendor", "$S", vendorName);
        }

        if(category != null) {
            extensionAnnotationSpec.addMember("category", CodeBlock.of("$T." + category, CATEGORY_CLASS));
        }

        extensionClassName = sanitizedExtensionName + "Connector";
        this.extensionType = ClassName.get(extensionPackage + ".internal", extensionClassName);
        this.extensionClassBuilder = getTypeSpec()
                .addModifiers(PUBLIC)
                .addAnnotation(extensionAnnotationSpec.build());
    }

    public ConnectionProviderBuilder withConnectionProvider(String name, ClassName connectionType) {
        this.connectionType = connectionType;
        ConnectionProviderBuilder connectionProviderBuilder = new ConnectionProviderBuilder(name, connectionType, this);
        connectionProviders.add(connectionProviderBuilder);
        return connectionProviderBuilder;
    }

    public OperationContainerBuilder withOperationContainer() {
        this.operationContainerBuilder = new OperationContainerBuilder(sanitizedExtensionName + "Operations", extensionType, connectionType, this);
        return operationContainerBuilder;
    }

    public SourceBuilder withSource(String name, TypeName payloadType, TypeName attributesTypes) {
        this.sourceBuilder = new SourceBuilder(sanitizedExtensionName + name + "Source", payloadType, attributesTypes, extensionType, connectionType, this);
        return sourceBuilder;
    }

    public ExtensionBuilder withAdditionalClass(JavaType extensionJavaType) {
        this.additionalClasses.add(extensionJavaType);
        return this;
    }

    @Override
    public String getName() {
        return extensionType.simpleName();
    }

    @Override
    public String getPackage() {
        return extensionType.packageName();
    }

    public String getExtensionPackage() {
        return extensionPackage;
    }

    @Override
    public TypeSpec doBuild() {
            AnnotationSpec.Builder connectionProvidersAnnotationBuilder = AnnotationSpec
                    .builder(CONNECTION_PROVIDERS_ANNOTATION);
            for (ConnectionProviderBuilder providerBuilder : connectionProviders) {
                connectionProvidersAnnotationBuilder.addMember("value", CodeBlock.of("$T.class", ClassName.get(providerBuilder.getPackage(), providerBuilder.getName())));
            }

            if(sourceBuilder != null) {
                extensionClassBuilder.addAnnotation(AnnotationSpec.builder(SOURCES_ANNOTATION)
                        .addMember("value", CodeBlock.of("$T.class", ClassName.get(sourceBuilder.getPackage(), sourceBuilder.getName())))
                        .build());
            }

            AnnotationSpec.Builder value = AnnotationSpec.builder(OPERATIONS_ANNOTATION).addMember("value", CodeBlock.of("$T.class", ClassName.get(operationContainerBuilder.getPackage(), operationContainerBuilder.getName())));

            extensionClassBuilder.addAnnotation(value.build());

            extensionClassBuilder.addAnnotation(connectionProvidersAnnotationBuilder
                    .build());

            for (FieldParameterBuilder parameter : parameters) {
                extensionClassBuilder.addField(parameter.build());
            }

            return extensionClassBuilder.build();
    }

    public void writeJavaFiles(VirtualFile srcJava) throws IOException {
        writeClass(srcJava, this);
        for (ConnectionProviderBuilder connectionProvider : connectionProviders) {
            writeClass(srcJava, connectionProvider);
        }
        writeClass(srcJava, operationContainerBuilder);
        if(sourceBuilder != null) {
            writeClass(srcJava, sourceBuilder);
        }

        for (JavaType additionalClass : additionalClasses) {
            writeClass(srcJava, additionalClass);
        }

    }

    private void writeClass(VirtualFile srcJava, JavaType extensionJavaType) throws IOException {
        VirtualFile extensionDirectory = VfsUtil.createDirectories(srcJava.getPath() + "/" + packageToPath(extensionJavaType.getPackage()));
        String source = JavaFile.builder(extensionJavaType.getPackage(), extensionJavaType.getType()).build().toString();
        extensionDirectory.findOrCreateChildData(this, extensionJavaType.getName() + ".java").setBinaryContent(source.getBytes());
    }

    private String packageToPath(String javaPackage) {
        return javaPackage.replace(".", "/");
    }
}
