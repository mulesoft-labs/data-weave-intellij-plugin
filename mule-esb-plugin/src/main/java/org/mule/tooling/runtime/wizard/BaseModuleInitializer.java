package org.mule.tooling.runtime.wizard;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;

import java.io.IOException;
import java.util.Properties;

public class BaseModuleInitializer {
    protected static final Logger LOG = Logger.getInstance(MuleAppModuleInitializer.class.getName());

    public static void runTemplate(Properties templateProps, String templateName, FileTemplateManager manager, VirtualFile pomFile) throws IOException {
        final FileTemplate template = manager.getInternalTemplate(templateName);
        final Properties defaultProperties = manager.getDefaultProperties();
        defaultProperties.putAll(templateProps);
        final String text = template.getText(defaultProperties);
        VfsUtil.saveText(pomFile, text);
    }

    @NotNull
    public static Properties createTemplateProperties(MavenId projectId, String muleVersion, String muleMavenPluginVersion, String mtfVersion) {
        final Properties templateProps = new Properties();
        templateProps.setProperty("GROUP_ID", projectId.getGroupId());
        templateProps.setProperty("ARTIFACT_ID", projectId.getArtifactId());
        templateProps.setProperty("VERSION", projectId.getVersion());
        templateProps.setProperty("MULE_VERSION", muleVersion);
        templateProps.setProperty("MULE_MAVEN_PLUGIN_VERSION", muleMavenPluginVersion);
        templateProps.setProperty("MTF_VERSION", mtfVersion);
        templateProps.setProperty("FILE_PATTERN", "${sys:mule.home}${sys:file.separator}logs${sys:file.separator}" + projectId.getArtifactId() + "-%i.log");
        templateProps.setProperty("FILE_NAME", "${sys:mule.home}${sys:file.separator}logs${sys:file.separator}" + projectId.getArtifactId() + ".log");
        return templateProps;
    }
}
