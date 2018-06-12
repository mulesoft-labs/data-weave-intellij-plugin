package org.mule.tooling.runtime.template;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.runtime.RuntimeIcons;

public class RuntimeTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String MUNIT_CONFIG_FILE = "Munit ConfigFile";
    public static final String SMART_CONNECTOR_CONFIG_FILE = "SmartConnector ConfigFile";
    public static final String SMART_CONNECTOR_POM_FILE = "SmartConnector Pom";

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

        group.addTemplate(new FileTemplateDescriptor(MULE_APP_CONFIG_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_POM_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_ARTIFACT_JSON_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(MULE_APP_LOG4J_FILE, RuntimeIcons.MuleSdk));
        return group;
    }
}
