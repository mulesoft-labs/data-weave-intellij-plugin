package org.mule.tooling.runtime.wizard.sdk;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.mule.tooling.runtime.wizard.SdkModuleBuilder.dasherize;

import org.mule.tooling.runtime.template.RuntimeTemplateManager;
import org.mule.tooling.runtime.wizard.BaseModuleInitializer;

import java.io.IOException;
import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

public class JavaSdkModuleInitializer extends BaseModuleInitializer {

    private static final Logger LOG = Logger.getInstance(JavaSdkModuleInitializer.class.getName());
    private static final String SRC_MAIN_RESOURCES = "/src/main/resources";
    private static final String SRC_MAIN_JAVA = "/src/main/java";
    private static final String SRC_TEST_MUNIT = "/src/test/munit";
    private static final String SRC_TEST_RESOURCES = "/src/test/resources";
    public static final ClassName RESULT_CLASS = ClassName.get("org.mule.runtime.extension.api.runtime.operation", "Result");

    public static void configure(final Project project, final MavenId projectId, final VirtualFile root, SdkProject sdkProject) {
        try {
            VirtualFile srcResources = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_RESOURCES);
            VirtualFile srcJava = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_JAVA);
            VirtualFile extensionPackage = VfsUtil.createDirectories(srcJava.getPath() + "/org/mule/connectors/internal");
            VirtualFile apiPackage = VfsUtil.createDirectories(srcJava.getPath() + "/org/mule/connectors/api");
            VirtualFile connectionPackage = VfsUtil.createDirectories(srcJava.getPath() + "/org/mule/connectors/internal/connection");
            VirtualFile operationsPackage = VfsUtil.createDirectories(srcJava.getPath() + "/org/mule/connectors/internal/operations");
            VirtualFile munit = VfsUtil.createDirectories(root.getPath() + SRC_TEST_MUNIT);
            VfsUtil.createDirectories(root.getPath() + SRC_TEST_RESOURCES);
            try {
                WriteCommandAction.writeCommandAction(project)
                        .withName("Creating Mule Module")
                        .run(new ThrowableRunnable<Throwable>() {
                            @Override
                            public void run() throws Throwable {
                                SdkType type = sdkProject.getType();
                                createJavaProject();
                            }

                            private void createJavaProject() throws IOException {
                                String extensionName = sdkProject.getName();
                                String extensionNameSpace = getExtensionNameSpace(extensionName);
                                String javaExtensionName = getJavaExtensionName(extensionName);
                                String defaultExtensionPackage = "org.mule.connectors";

                                Properties properties = createTemplateProperties(extensionName, extensionNameSpace, projectId, sdkProject);

                                createPomXml(extensionNameSpace, properties);

                                ClassName stringClass = ClassName.get(String.class);
                                JavaFile connectionClass = createConnection(javaExtensionName, defaultExtensionPackage + ".internal.connection", stringClass);
                                JavaFile attributesClass = createAttributes(javaExtensionName,  defaultExtensionPackage + ".api", stringClass);

                                ExtensionBuilder extensionBuilder = new ExtensionBuilder(javaExtensionName, null, null, defaultExtensionPackage);
                                ClassName connectionType = classNameFrom(connectionClass);
                                ClassName attributesType = classNameFrom(attributesClass);

                                ExtensionBuilder.ConnectionProviderBuilder connectionProviderBuilder = createConnectionProvider(javaExtensionName, stringClass, extensionBuilder, connectionType);
                                ExtensionBuilder.OperationContainerBuilder operationContainerBuilder = createOperations(stringClass, extensionBuilder, attributesType);

                                connectionPackage.findOrCreateChildData(this, connectionClass.typeSpec.name + ".java").setBinaryContent(connectionClass.toString().getBytes());

                                apiPackage.findOrCreateChildData(this, attributesClass.typeSpec.name + ".java").setBinaryContent(attributesClass.toString().getBytes());

                                JavaFile extensionClass = JavaFile.builder(extensionBuilder.getPackage(), extensionBuilder.build()).build();
                                extensionPackage.findOrCreateChildData(this, extensionBuilder.getName() + ".java").setBinaryContent(extensionClass.toString().getBytes());

                                JavaFile connectionProviderClass = JavaFile.builder(connectionProviderBuilder.getPackage(), connectionProviderBuilder.build()).build();
                                connectionPackage.findOrCreateChildData(this, connectionProviderBuilder.getName() + ".java").setBinaryContent(connectionProviderClass.toString().getBytes());

                                JavaFile operationContainerClass = JavaFile.builder(operationContainerBuilder.getPackage(), operationContainerBuilder.build()).build();
                                operationsPackage.findOrCreateChildData(this, operationContainerBuilder.getName() + ".java").setBinaryContent(operationContainerClass.toString().getBytes());
                            }

                            @NotNull
                            private ExtensionBuilder.OperationContainerBuilder createOperations(ClassName stringClass, ExtensionBuilder extensionBuilder, ClassName attributesType) {
                                ExtensionBuilder.OperationContainerBuilder operationContainerBuilder = extensionBuilder.withOperationContainer();

                                operationContainerBuilder.withOperation("postMessage")
                                        .withConnection("connection")
                                        .withParameter("message", stringClass)
                                        .withParameter("destination", stringClass)
                                        .returns(ParameterizedTypeName.get(RESULT_CLASS, stringClass, attributesType))
                                        .withMediaType("*/*")
                                        .withStatement(CodeBlock.builder().add("$T attributes = new $T(connection.getClientId(), destination)", attributesType, attributesType).build())
                                        .withStatement(CodeBlock.builder()
                                                .add("return $T.<$T,$T>builder()", RESULT_CLASS, stringClass, attributesType)
                                                .add("\n.output(message)")
                                                .add("\n.attributes(attributes)")
                                                .add("\n.build()")
                                                .build());

                                operationContainerBuilder.withOperation("helloWorld")
                                        .withConnection("connection")
                                        .withParameter("name", stringClass)
                                        .withMediaType("*/*")
                                        .returns(stringClass)
                                        .withStatement(CodeBlock.builder()
                                                .add("return \"Hello World!, \" + name")
                                                .build());
                                return operationContainerBuilder;
                            }

                            @NotNull
                            private ExtensionBuilder.ConnectionProviderBuilder createConnectionProvider(String javaExtensionName, ClassName stringClass, ExtensionBuilder extensionBuilder, ClassName connectionType) {
                                ExtensionBuilder.ConnectionProviderBuilder connectionProviderBuilder = extensionBuilder.withConnectionProvider(javaExtensionName + "ConnectionProvider", connectionType);

                                connectionProviderBuilder.withParameter("clientId", stringClass);
                                connectionProviderBuilder.withConnectMethod(CodeBlock.builder().addStatement("return new $T(this.clientId)", connectionType).build());
                                connectionProviderBuilder.withJavadoc(CodeBlock.builder()
                                        .addStatement("This is a Connection Provider, is executed to obtain new connections when an operation or message source requires.")
                                        .build());
                                connectionProviderBuilder.withMethod("getClientId")
                                        .returns(stringClass)
                                        .withStatement(CodeBlock.builder().add("return this.clientId").build());
                                return connectionProviderBuilder;
                            }

                            private void createPomXml(String extensionNameSpace, Properties properties) throws IOException {
                                FileTemplateManager manager = FileTemplateManager.getInstance(project);

                                if(sdkProject.isCreateMTFTests()) {
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_POM_MTF_FILE, manager, root.findOrCreateChildData(this, MavenConstants.POM_XML));
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_MTF_TEST_FILE, manager, munit.findOrCreateChildData(this, extensionNameSpace + "-test-case.xml"));
                                } else {
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_POM_FILE, manager, root.findOrCreateChildData(this, MavenConstants.POM_XML));
                                }
                            }


                        });
            } catch (Throwable throwable) {
                LOG.error(throwable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ClassName classNameFrom(JavaFile javaFile) {
        return ClassName.get(javaFile.packageName, javaFile.typeSpec.name);
    }

    @NotNull
    private static Properties createTemplateProperties(String extensionName, String extensionNameSpace, MavenId projectId, SdkProject sdkProject) {
        Properties properties = new Properties();
        properties.setProperty("GROUP_ID", projectId.getGroupId());
        properties.setProperty("ARTIFACT_ID", projectId.getArtifactId());
        properties.setProperty("VERSION", projectId.getVersion());
        properties.setProperty("PARENT_VERSION", sdkProject.getVersion());
        properties.setProperty("EXT_NAME", extensionName);
        properties.setProperty("EXT_NAMESPACE", extensionNameSpace);
        return properties;
    }

    private static String getExtensionNameSpace(String extensionName) {
        String outputName = dasherize(extensionName);
        if(extensionName.toLowerCase().endsWith("connector")) {
            outputName = outputName.substring(0, extensionName.length() - 10);
        } else if (extensionName.toLowerCase().endsWith("module")) {
            outputName = outputName.substring(0, extensionName.length() - 7);
        }
        return outputName;
    }

    @NotNull
    private static String getJavaExtensionName(String extensionName) {
        if(extensionName.toLowerCase().endsWith("connector")) {
            extensionName = extensionName.substring(0, extensionName.length() - 9);
        } else if (extensionName.toLowerCase().endsWith("module")) {
            extensionName = extensionName.substring(0, extensionName.length() - 6);
        }

        extensionName = extensionName.replace(" ", "");
        extensionName = extensionName.replace("-", "");
        return extensionName;
    }

    @NotNull
    private static JavaFile createAttributes(String javaExtensionName, String classPackage, ClassName stringClass) {
        TypeSpec attributes = TypeSpec.classBuilder(javaExtensionName + "PublishAttributes")
                .addModifiers(PUBLIC)
                .addField(stringClass, "clientId", PRIVATE)
                .addField(stringClass, "destination", PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(stringClass, "clientId")
                        .addParameter(stringClass, "destination")
                        .addStatement("this.clientId = clientId")
                        .addStatement("this.destination = destination")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getClientId")
                        .returns(stringClass)
                        .addStatement("return this.clientId")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getDestination")
                        .returns(stringClass)
                        .addStatement("return this.destination")
                        .build()).build();
        return JavaFile.builder(classPackage, attributes).build();
    }

    @NotNull
    private static JavaFile createConnection(String javaExtensionName, String classPackage, ClassName stringClass) {
        TypeSpec connection = TypeSpec.classBuilder(javaExtensionName + "Connection")
                .addModifiers(PUBLIC)
                .addField(stringClass, "clientId", PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(stringClass, "clientId")
                        .addStatement("this.clientId = clientId")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getClientId")
                        .addModifiers(PUBLIC)
                        .returns(stringClass)
                        .addStatement("return this.clientId")
                        .build())
                .build();

        return JavaFile.builder(classPackage, connection).build();
    }
}
