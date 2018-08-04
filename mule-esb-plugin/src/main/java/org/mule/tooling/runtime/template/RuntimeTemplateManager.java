package org.mule.tooling.runtime.template;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.runtime.RuntimeIcons;

public class RuntimeTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String MUNIT_CONFIG_FILE = "Munit ConfigFile";
    public static final String SMART_CONNECTOR_CONFIG_FILE = "SmartConnector ConfigFile";
    public static final String SMART_CONNECTOR_POM_FILE = "SmartConnector Pom";

    public static final String SDK_JAVA_POM_FILE = "JavaConnector Pom";
    public static final String SDK_JAVA_POM_MTF_FILE = "JavaConnector PomMTF";
    public static final String SDK_JAVA_MTF_TEST_FILE = "JavaConnector MTFTestCase";
    public static final String SDK_JAVA_MTF_SOURCE_TEST_FILE = "JavaConnector MTFSourceTestCase";
    public static final String SDK_JAVA_MTF_SHARED_CONFIG_TEST_FILE = "JavaConnector MTFSharedConfig";
    public static final String SDK_JAVA_LOG4J_TEST_FILE = "JavaConnector Log4j2-test";

    public static final String MULE_APP_ARTIFACT_JSON_FILE = "MuleApp Artifact";
    public static final String MULE_APP_CONFIG_FILE = "MuleApp ConfigFile";
    public static final String MULE_APP_LOG4J_FILE = "MuleApp Log4j";
    public static final String MULE_APP_POM_FILE = "MuleApp Pom";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Mule Sdk", RuntimeIcons.MuleSdk);
        group.addTemplate(new FileTemplateDescriptor(MUNIT_CONFIG_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SMART_CONNECTOR_CONFIG_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SMART_CONNECTOR_POM_FILE, RuntimeIcons.MuleSdk));

        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_POM_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_POM_MTF_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_MTF_TEST_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_MTF_SOURCE_TEST_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_MTF_SHARED_CONFIG_TEST_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SDK_JAVA_LOG4J_TEST_FILE, RuntimeIcons.MuleSdk));

        group.addTemplate(new FileTemplateDescriptor(MULE_APP_CONFIG_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_POM_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_ARTIFACT_JSON_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_LOG4J_FILE, RuntimeIcons.MuleSdk));
        return group;
    }
}
