package org.mule.tooling.runtime.wizard.sdk;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.mule.tooling.runtime.wizard.SdkModuleBuilder.dasherize;
import static org.mule.tooling.runtime.wizard.sdk.builder.BuilderUtils.mediaTypeOf;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.LOGGER;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.LOGGER_FACTORY;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.RESULT;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SCHEDULER_SERVICE;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SOURCE_CALLBACK_CONTEXT;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.SOURCE_RESULT;

import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.tooling.runtime.template.RuntimeTemplateManager;
import org.mule.tooling.runtime.wizard.BaseModuleInitializer;
import org.mule.tooling.runtime.wizard.sdk.builder.ConnectionProviderBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.JavaType;
import org.mule.tooling.runtime.wizard.sdk.builder.OperationContainerBuilder;
import org.mule.tooling.runtime.wizard.sdk.builder.SourceBuilder;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
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

    public static void configure(final Project project, final MavenId projectId, final VirtualFile root, SdkProject sdkProject) {
        try {
            VirtualFile srcResources = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_RESOURCES);
            VirtualFile srcJava = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_JAVA);
            VirtualFile munit = VfsUtil.createDirectories(root.getPath() + SRC_TEST_MUNIT);
            VirtualFile testResources = VfsUtil.createDirectories(root.getPath() + SRC_TEST_RESOURCES);
            try {
                WriteCommandAction.writeCommandAction(project)
                        .withName("Creating Mule Module")
                        .run(new ThrowableRunnable<Throwable>() {
                            @Override
                            public void run() throws Throwable {
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
                                ExtensionBuilder extensionBuilder = new ExtensionBuilder(javaExtensionName, null, null, defaultExtensionPackage);

                                JavaType connectionClass = createConnection(javaExtensionName, defaultExtensionPackage + ".internal.connection", stringClass);
                                JavaType attributesClass = createAttributes(javaExtensionName, defaultExtensionPackage + ".api", stringClass);

                                extensionBuilder.withAdditionalClass(connectionClass);
                                extensionBuilder.withAdditionalClass(attributesClass);

                                ClassName connectionType = classNameFrom(connectionClass);
                                ClassName attributesType = classNameFrom(attributesClass);

                                createConnectionProvider(javaExtensionName, stringClass, extensionBuilder, connectionType);
                                createOperations(stringClass, extensionBuilder, attributesType);


                                if (sdkProject.isCreateSource()) {
                                    createMessageSource(stringClass, extensionBuilder);
                                }

                                extensionBuilder.writeJavaFiles(srcJava);
                            }

                            private ClassName classNameFrom(JavaType connectionClass) {
                                return ClassName.get(connectionClass.getPackage(), connectionClass.getName());
                            }

                            @NotNull
                            private JavaFile createMessageSource(ClassName payloadType, ExtensionBuilder extensionBuilder) {
                                ClassName attributesTypes = ClassName.get(Void.class);
                                SourceBuilder source = extensionBuilder.withSource("Message", payloadType, attributesTypes);

                                source.withAnnotation(mediaTypeOf("*/*"));
                                FieldSpec counterVariableField = FieldSpec
                                        .builder(payloadType, "COUNTER_VARIABLE", PRIVATE, STATIC, FINAL)
                                        .initializer("$S", "counter")
                                        .build();
                                source.withField(counterVariableField);
                                FieldSpec logger = FieldSpec.builder(LOGGER, "LOGGER", PRIVATE, STATIC, FINAL)
                                        .initializer("$T.getLogger($T.class)", LOGGER_FACTORY, source.getClassName())
                                        .build();
                                source.withField(logger);
                                source.withParameter("message", payloadType);
                                source.withParameter("period", LONG).asOptional("1000");
                                source.withParameter("timeUnit", ClassName.get(TimeUnit.class))
                                        .asOptional("MILLISECONDS");
                                source.withField(FieldSpec
                                        .builder(SCHEDULER_SERVICE, "schedulerService")
                                        .addAnnotation(Inject.class)
                                        .build());

                                FieldSpec scheduler = FieldSpec
                                        .builder(Scheduler.class, "scheduler")
                                        .build();

                                source.withField(scheduler);

                                String sourceRunnableClass = "SourceRunnable";

                                FieldSpec counterField = FieldSpec.builder(INT, "counter")
                                        .initializer("0")
                                        .build();

                                source.getTypeSpec().addType(TypeSpec.classBuilder(sourceRunnableClass)
                                        .addSuperinterface(Runnable.class)
                                        .addField(source.getSourceCallbackType(), "sourceCallback")
                                        .addField(source.getConnectionType(), "connection")
                                        .addField(counterField)
                                        .addMethod(constructorBuilder()
                                                .addParameter(source.getSourceCallbackType(), "sourceCallback")
                                                .addParameter(source.getConnectionType(), "connection")
                                                .addCode(CodeBlock.builder()
                                                        .addStatement("this.sourceCallback = sourceCallback")
                                                        .addStatement("this.connection = connection")
                                                        .build())
                                                .build())
                                        .addMethod(methodBuilder("run")
                                                .addModifiers(PUBLIC)
                                                .addCode(CodeBlock.builder()
                                                        .addStatement("$T context = sourceCallback.createContext()", SOURCE_CALLBACK_CONTEXT)
                                                        .addStatement("context.addVariable($N, $N++)", counterVariableField, counterField)
                                                        .build())
                                                .addStatement(CodeBlock.builder()
                                                        .add("sourceCallback.handle($T.<$T,$T>builder()", RESULT, payloadType, attributesTypes)
                                                        .add(".output(message)")
                                                        .add(".build(), context)")
                                                        .build())
                                                .build())
                                        .build());

                                source.onStart()
                                        .addJavadoc("This method is called to start the Message Source.\n")
                                        .addJavadoc("The Source is considered Started once the onStart method finished, so it's required to start a new thread to\n" +
                                                "execute the source logic.\n")
                                        .addJavadoc("In this case the SchedulerService is used to schedule at a fixed rate to execute the {@link SourceRunnable} \n")
                                        .addParameter(ParameterSpec
                                                .builder(source.getSourceCallbackType(), "sourceCallback")
                                                .build())
                                        .addCode(CodeBlock.builder()
                                                .addStatement("scheduler = schedulerService.cpuLightScheduler()")
                                                .addStatement("$T connectionInstance = connection.connect()", source.getConnectionType())
                                                .addStatement("scheduler.scheduleAtFixedRate(new $L(sourceCallback, connectionInstance), 0, period, timeUnit)", sourceRunnableClass)
                                                .build())
                                        .build();

                                source.onStop()
                                        .addCode(CodeBlock.builder()
                                                .addStatement("$N.info(\"Stopping Source\")", logger)
                                                .addStatement("$N.stop()", scheduler)
                                                .addStatement("$N.shutdown()", scheduler)
                                                .build());

                                ParameterSpec sourceCallbackContext = ParameterSpec.builder(SOURCE_CALLBACK_CONTEXT, "sourceCallbackContext").build();
                                source.onSuccess()
                                        .addParameter(sourceCallbackContext)
                                        .addCode(CodeBlock.builder()
                                                .addStatement("$N.info($T.format(\"The message number '%s' has been processed correctly\", $N.getVariable($N).get()));", logger, String.class, sourceCallbackContext, counterVariableField)
                                                .build());

                                source.onError()
                                        .addParameter(sourceCallbackContext)
                                        .addCode(CodeBlock.builder()
                                                .addStatement("$N.info($T.format(\"An error occurred processing the message number '%s'\", $N.getVariable($N).get()));",logger, String.class, sourceCallbackContext, counterVariableField)
                                                .build());

                                ParameterSpec sourceResult = ParameterSpec.builder(SOURCE_RESULT, "sourceResult").build();

                                source.onTerminate()
                                        .addParameter(sourceResult)
                                        .addCode(CodeBlock.builder()
                                        .addStatement("$N.info(\"Terminated message processing with Status: \" + ($N.isSuccess() ? $S : $S) )", logger, sourceResult, "OK", "FAILURE")
                                        .build());

                                return JavaFile.builder(source.getPackage(), source.build()).build();
                            }

                            @NotNull
                            private OperationContainerBuilder createOperations(ClassName stringClass, ExtensionBuilder extensionBuilder, ClassName attributesType) {

                                OperationContainerBuilder operationContainerBuilder = extensionBuilder.withOperationContainer();

                                operationContainerBuilder.withOperation("postMessage")
                                        .withConnection("connection")
                                        .withParameter("message", stringClass)
                                        .withParameter("destination", stringClass)
                                        .returns(ParameterizedTypeName.get(RESULT, stringClass, attributesType))
                                        .withMediaType("*/*")
                                        .withStatement(CodeBlock.builder().add("$T attributes = new $T(connection.getClientId(), destination)", attributesType, attributesType).build())
                                        .withStatement(CodeBlock.builder()
                                                .add("return $T.<$T,$T>builder()", RESULT, stringClass, attributesType)
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
                            private ConnectionProviderBuilder createConnectionProvider(String javaExtensionName, ClassName stringClass, ExtensionBuilder extensionBuilder, ClassName connectionType) {
                                ConnectionProviderBuilder connectionProviderBuilder = extensionBuilder.withConnectionProvider(javaExtensionName + "ConnectionProvider", connectionType);

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

                                runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_LOG4J_TEST_FILE, manager, testResources.findOrCreateChildData(this, "log4j2-test.xml"));
                                if (sdkProject.isCreateMTFTests()) {
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_POM_MTF_FILE, manager, root.findOrCreateChildData(this, MavenConstants.POM_XML));
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_MTF_TEST_FILE, manager, munit.findOrCreateChildData(this, extensionNameSpace + "-test-case.xml"));
                                    runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_MTF_SHARED_CONFIG_TEST_FILE, manager, munit.findOrCreateChildData(this, extensionNameSpace + "-shared-config.xml"));
                                    if(sdkProject.isCreateSource()) {
                                        runTemplate(properties, RuntimeTemplateManager.SDK_JAVA_MTF_SOURCE_TEST_FILE, manager, munit.findOrCreateChildData(this, extensionNameSpace + "-source-test-case.xml"));
                                    }
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
        if (extensionName.toLowerCase().endsWith("connector")) {
            outputName = outputName.substring(0, extensionName.length() - 10);
        } else if (extensionName.toLowerCase().endsWith("module")) {
            outputName = outputName.substring(0, extensionName.length() - 7);
        }
        return outputName;
    }

    @NotNull
    private static String getJavaExtensionName(String extensionName) {
        if (extensionName.toLowerCase().endsWith("connector")) {
            extensionName = extensionName.substring(0, extensionName.length() - 9);
        } else if (extensionName.toLowerCase().endsWith("module")) {
            extensionName = extensionName.substring(0, extensionName.length() - 6);
        }

        extensionName = extensionName.replace(" ", "");
        extensionName = extensionName.replace("-", "");
        return extensionName;
    }

    @NotNull
    private static JavaType createAttributes(String javaExtensionName, String classPackage, ClassName stringClass) {
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

        return JavaType.create(attributes, classPackage);
    }

    @NotNull
    private static JavaType createConnection(String javaExtensionName, String classPackage, ClassName stringClass) {
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

        return JavaType.create(connection, classPackage);
    }
}
