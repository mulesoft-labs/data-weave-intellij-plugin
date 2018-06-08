package org.mule.tooling.runtime.schema;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.psi.PsiFile;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlSchemaProvider;
import com.intellij.xml.impl.schema.XmlNSDescriptorImpl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MuleSchemaProvider extends XmlSchemaProvider {

    private static final Logger LOG = Logger.getInstance(MuleSchemaProvider.class.getName());

    public MuleSchemaProvider() {
        super();
    }


    @Override
    public boolean isAvailable(@NotNull XmlFile file) {
        return true;
    }

    /**
     * Looks for the schema file to handle the given namespace (url) within the schemas supported by this provider.
     * These schemas are read from spring.schemas file and searched in project files and dependencies. If a schema
     * declared in spring.schemas is not present within project files and project dependencies it will not be resolved.
     *
     * @param url      the url of the namespace
     * @param module   the module where the baseFile is
     * @param baseFile the file where the namespace is declared
     * @return the schema file for the given url if it is supported by this provider (declared in spring.schemas), otherwise null
     */
    @Override
    public XmlFile getSchema(@NotNull @NonNls String url, @Nullable final Module module, @NotNull PsiFile baseFile) throws ProcessCanceledException {
        Module fileModule;
        if (module != null) {
            fileModule = module;
        } else {
            final Module[] modules = ModuleManager.getInstance(baseFile.getProject()).getModules();
            //TODO search the right module
            fileModule = modules[0];
        }
        final MuleSchemaManager instance = MuleSchemaManager.getInstance(fileModule);
        final Optional<XmlFile> schema = instance.getSchema(url);
        return schema.orElse(null);
    }


    @Override
    @NotNull
    public Set<String> getAvailableNamespaces(@NotNull XmlFile file, @Nullable String tagName) {
        final Set<String> namespaces = new HashSet<>();
        if (StringUtils.isNotEmpty(tagName)) {
            final Module fileModule = getModule(file);
            final List<MuleSchemaManager.XmlInfo> schemas = MuleSchemaManager.getInstance(fileModule).getSchemas();
            try {
                for (MuleSchemaManager.XmlInfo xsd : schemas) {
                    final XmlDocument document = xsd.getSchemaFile().getDocument();
                    if (document != null) {
                        final PsiMetaData metaData = document.getMetaData();
                        if (metaData instanceof XmlNSDescriptorImpl) {
                            XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
                            String defaultNamespace = descriptor.getDefaultNamespace();
                            if (StringUtils.isNotEmpty(defaultNamespace)) {
                                XmlElementDescriptor elementDescriptor = descriptor.getElementDescriptor(tagName, defaultNamespace);
                                if (elementDescriptor != null) {
                                    namespaces.add(defaultNamespace);
                                }
                            } else {
                                namespaces.add(defaultNamespace);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.warn(e);
            }
        }
        return namespaces;
    }

    public Module getModule(@NotNull XmlFile file) {
        final Module fileModule;
        final Module module = ModuleUtil.findModuleForFile(file);
        if (module != null) {
            fileModule = module;
        } else {
            final Module[] modules = ModuleManager.getInstance(file.getProject()).getModules();
            //TODO search the right module
            fileModule = modules[0];
        }
        return fileModule;
    }

    @Nullable
    @Override
    public String getDefaultPrefix(@NotNull String namespace, @NotNull XmlFile context) {
        String[] split = namespace.split("/");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return super.getDefaultPrefix(namespace, context);
    }

    @Override
    public Set<String> getLocations(@NotNull @NonNls final String namespace, @NotNull final XmlFile context) throws ProcessCanceledException {
        Set<String> locations = new HashSet<>();
        final Module module = ModuleUtil.findModuleForFile(context);
        if (module == null) {
            return null;
        }
        try {
            Optional<String> schema = MuleSchemaManager.getInstance(module).getSchemaLocation(namespace);
            schema.ifPresent(locations::add);
        } catch (Exception e) {
            LOG.warn(e);
        }
        return locations;
    }
}