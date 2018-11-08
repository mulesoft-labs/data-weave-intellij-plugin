package org.mule.tooling.lang.raml.file;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.lang.raml.util.RamlIcons;

public class RamlFilesTemplateManager implements FileTemplateGroupDescriptorFactory {
    public static final String RAML_FILE = "RAML File";
    public static final String ANNOTATION_TYPE_DECLARATION = "Annotation Type Declaration";
    public static final String DOCUMENTATION_ITEM = "Documentation Item";
    public static final String DATA_TYPE = "Data Type";
    public static final String EXTENSION = "Extension";
    public static final String LIBRARY = "Library";
    public static final String NAMED_EXAMPLE = "Named Example";
    public static final String OVERLAY = "Overlay";
    public static final String RESOURCE_TYPE = "Resource Type";
    public static final String SECURITY_SCHEME = "Security Scheme";
    public static final String TRAIT = "Trait";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("RAML", RamlIcons.RamlFileType);
        group.addTemplate(new FileTemplateDescriptor(RAML_FILE, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(ANNOTATION_TYPE_DECLARATION, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(DOCUMENTATION_ITEM, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(DATA_TYPE, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(EXTENSION, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(LIBRARY, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(NAMED_EXAMPLE, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(OVERLAY, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(RESOURCE_TYPE, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(SECURITY_SCHEME, RamlIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(TRAIT, RamlIcons.RamlFileType));
        return group;
    }
}