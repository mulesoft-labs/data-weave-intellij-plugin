package org.mule.tooling.runtime.schema;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
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
import org.mule.tooling.runtime.tooling.MuleRuntimeServerManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MuleSchemaProvider extends XmlSchemaProvider {

  private static final Logger LOG = Logger.getInstance(MuleSchemaProvider.class.getName());

  public MuleSchemaProvider() {
    super();
  }


  @Override
  public boolean isAvailable(@NotNull XmlFile file) {
    //This should be either a schema file or an Mule Config or Mule Domain or Mule Module
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
    final Optional<SchemaInformation> schema;
    if (module != null) {
      final MuleModuleSchemaProvider instance = MuleModuleSchemaProvider.getInstance(module);
      schema = instance.getSchema(url);
      return schema.flatMap((info) -> info.getSchemaAsXmlFile(baseFile.getProject())).orElse(null);
    } else {
      //This file is not form any module then we use the global search
      String muleVersion = MuleRuntimeServerManager.getMuleVersionOf(baseFile.getProject());
      schema = MuleSchemaRepository.getInstance(muleVersion).searchSchemaByUrl(url);
    }
    return schema.flatMap((info) -> info.getSchemaAsXmlFile(baseFile.getProject())).orElse(null);
  }


  @Override
  @NotNull
  public Set<String> getAvailableNamespaces(@NotNull XmlFile file, @Nullable String tagName) {
    final Set<String> namespaces = new HashSet<>();
    final Module fileModule = ModuleUtil.findModuleForFile(file);
    if (fileModule != null) {
      final Collection<SchemaInformation> schemas = MuleModuleSchemaProvider.getInstance(fileModule).getSchemas();
      if (StringUtils.isNotEmpty(tagName)) {
        try {
          for (SchemaInformation xsd: schemas) {
            final Optional<XmlFile> schemaAsXmlFile = xsd.getSchemaAsXmlFile(file.getProject());
            if (schemaAsXmlFile.isPresent()) {
              final XmlDocument document = schemaAsXmlFile.get().getDocument();
              if (document != null) {
                final PsiMetaData metaData = document.getMetaData();
                if (metaData instanceof XmlNSDescriptorImpl) {
                  final XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
                  final String defaultNamespace = descriptor.getDefaultNamespace();
                  if (StringUtils.isNotEmpty(defaultNamespace)) {
                    final XmlElementDescriptor elementDescriptor = descriptor.getElementDescriptor(tagName, defaultNamespace);
                    if (elementDescriptor != null) {
                      namespaces.add(defaultNamespace);
                    }
                  } else {
                    namespaces.add(defaultNamespace);
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          LOG.warn(e);
        }
      } else {
        for (SchemaInformation schema: schemas) {
          namespaces.add(schema.getNamespace());
        }
      }
    }
    return namespaces;
  }


  @Nullable
  @Override
  public String getDefaultPrefix(@NotNull String namespace, @NotNull XmlFile context) {
    final Module fileModule = ModuleUtil.findModuleForFile(context);
    if (fileModule != null) {
      return MuleModuleSchemaProvider.getInstance(fileModule).getPrefixFor(namespace);
    } else {
      String[] split = namespace.split("/");
      if (split.length > 0) {
        return split[split.length - 1];
      } else {
        return super.getDefaultPrefix(namespace, context);
      }
    }
  }

  @Override
  public Set<String> getLocations(@NotNull @NonNls final String namespace, @NotNull final XmlFile context) throws ProcessCanceledException {

    Set<String> locations = new HashSet<>();
    final Module module = ModuleUtil.findModuleForFile(context);
    if (module == null) {
      return null;
    }
    try {
      Optional<String> schema = MuleModuleSchemaProvider.getInstance(module).getSchemaLocation(namespace);
      schema.ifPresent(locations::add);
    } catch (Exception e) {
      LOG.warn(e);
    }
    return locations;
  }


}
