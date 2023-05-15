package org.mule.tooling.gcl;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.restsdk.utils.RestSdkIcons;

public class GCLFileTemplateManager implements FileTemplateGroupDescriptorFactory {

    public static final String GCL = "GCL";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor(GCL, RestSdkIcons.RamlFileType);
        group.addTemplate(new FileTemplateDescriptor(GCL, RestSdkIcons.OASFileType));
        return group;
    }
}
