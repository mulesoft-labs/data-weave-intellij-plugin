package org.mule.tooling.lang.dw.templates;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import icons.OpenapiIcons;
import org.mule.tooling.lang.dw.WeaveIcons;

public class WeaveFilesTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String DATA_WEAVE_FILE = "DataWeave Mapping";
    public static final String DATA_WEAVE_MODULE_FILE = "DataWeave Module";
    public static final String DATA_WEAVE_UNIT_FILE = "DataWeave UnitTest";
    public static final String BAT_FILE = "BAT";
    public static final String WEAVE_MAVEN_MODULE = "Weave Maven Module";
    public static final String REST_SDK_MAVEN_MODULE = "REST SDK POM Template";
    public static final String CONNECTOR_DESCRIPTOR = "REST SDK Connector Descriptor";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Data Weave", WeaveIcons.DataWeaveIcon);
        group.addTemplate(new FileTemplateDescriptor(DATA_WEAVE_FILE, WeaveIcons.DataWeaveIcon));
        group.addTemplate(new FileTemplateDescriptor(DATA_WEAVE_MODULE_FILE, WeaveIcons.DataWeaveIcon));
        group.addTemplate(new FileTemplateDescriptor(DATA_WEAVE_UNIT_FILE, WeaveIcons.DataWeaveIcon));
        group.addTemplate(new FileTemplateDescriptor(BAT_FILE, WeaveIcons.DataWeaveIcon));
        group.addTemplate(new FileTemplateDescriptor(WEAVE_MAVEN_MODULE, OpenapiIcons.RepositoryLibraryLogo));

        //RestSDK Templates
        group.addTemplate(new FileTemplateDescriptor(CONNECTOR_DESCRIPTOR, OpenapiIcons.RepositoryLibraryLogo));
        group.addTemplate(new FileTemplateDescriptor(REST_SDK_MAVEN_MODULE, OpenapiIcons.RepositoryLibraryLogo));
        return group;
    }
}