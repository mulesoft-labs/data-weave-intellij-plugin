package org.mule.tooling.runtime.template;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.runtime.RuntimeIcons;

public class RuntimeTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String Munit_FILE = "Munit File";
    public static final String SMART_CONNECTOR_FILE = "SmartConnector";
    public static final String SMART_CONNECTOR_MODULE_FILE = "SmartConnector Module";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Mule Sdk", RuntimeIcons.MuleSdk);
        group.addTemplate(new FileTemplateDescriptor(Munit_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SMART_CONNECTOR_FILE, RuntimeIcons.MuleSdk));
        group.addTemplate(new FileTemplateDescriptor(SMART_CONNECTOR_MODULE_FILE, RuntimeIcons.MuleSdk));
        return group;
    }
}
