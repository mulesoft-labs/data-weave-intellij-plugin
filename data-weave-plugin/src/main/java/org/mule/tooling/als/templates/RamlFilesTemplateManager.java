package org.mule.tooling.als.templates;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import org.mule.tooling.als.utils.RestSdkIcons;


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
    public static final String OAS3 = "OAS3";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("RAML", RestSdkIcons.RamlFileType);
        group.addTemplate(new FileTemplateDescriptor(OAS3, RestSdkIcons.OASFileType));
        group.addTemplate(new FileTemplateDescriptor(RAML_FILE, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(ANNOTATION_TYPE_DECLARATION, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(DOCUMENTATION_ITEM, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(DATA_TYPE, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(EXTENSION, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(LIBRARY, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(NAMED_EXAMPLE, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(OVERLAY, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(RESOURCE_TYPE, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(SECURITY_SCHEME, RestSdkIcons.RamlFileType));
        group.addTemplate(new FileTemplateDescriptor(TRAIT, RestSdkIcons.RamlFileType));
        return group;
    }
}