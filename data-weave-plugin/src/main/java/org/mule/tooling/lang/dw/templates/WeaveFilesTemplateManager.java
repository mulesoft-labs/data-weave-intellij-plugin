package org.mule.tooling.lang.dw.templates;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import icons.MavenIcons;
import org.mule.tooling.lang.dw.WeaveIcons;

public class WeaveFilesTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String DATA_WEAVE_FILE = "Data Weave";
    public static final String WEAVE_MAVEN_MODULE = "Weave Maven Module";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Data Weave", WeaveIcons.DataWeaveIcon);
        group.addTemplate(new FileTemplateDescriptor(DATA_WEAVE_FILE, WeaveIcons.DataWeaveIcon));
        group.addTemplate(new FileTemplateDescriptor(WEAVE_MAVEN_MODULE, MavenIcons.MavenLogo));
        return group;
    }
}