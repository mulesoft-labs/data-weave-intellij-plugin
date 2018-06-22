package org.mule.tooling.runtime.schema;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.ExtendedTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlExtension;
import com.intellij.xml.impl.schema.XmlNSDescriptorImpl;
import com.intellij.xml.util.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.util.StreamUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class MuleCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiElement parent = parameters.getPosition().getParent();
    if (parent instanceof XmlTag) {
      final XmlTag xmlTag = (XmlTag) parent;
      final Module module = ModuleUtil.findModuleForFile(parameters.getOriginalFile());
      if (module != null) {
        final String namespacePrefix = xmlTag.getNamespacePrefix();
        final Project project = xmlTag.getProject();
        final MuleModuleSchemaProvider schemaProvider = MuleModuleSchemaProvider.getInstance(module);
        final Optional<SchemaInformation> schemaFromPrefix = schemaProvider.getSchemaFromPrefix(namespacePrefix);
        if (schemaFromPrefix.isPresent()) {
          final SchemaInformation schemaInformation = schemaFromPrefix.get();
          addElementsFrom(project, schemaInformation, xmlTag, schemaProvider, result);
        } else {
          Collection<SchemaInformation> schemas = schemaProvider.getSchemas().stream().filter(StreamUtils.distinctByKey(SchemaInformation::getNamespace)).collect(Collectors.toList());
          for (SchemaInformation schema: schemas) {
            addElementsFrom(project, schema, xmlTag, schemaProvider, result);
          }
        }
      }
    }
  }

  private void addElementsFrom(Project project, SchemaInformation schemaInformation, XmlTag xmlTag, MuleModuleSchemaProvider schemaProvider, @NotNull CompletionResultSet result) {
    final Optional<XmlFile> schemaAsXmlFile = schemaInformation.getSchemaAsXmlFile(project);
    if (schemaAsXmlFile.isPresent()) {
      XmlFile file = schemaAsXmlFile.get();
      addPossibleTagsFrom(file, xmlTag, schemaProvider, result);
    }
  }

  private void addPossibleTagsFrom(XmlFile file, XmlTag xmlTag, MuleModuleSchemaProvider schemaProvider, @NotNull CompletionResultSet result) {
    final XmlDocument document = file.getDocument();
    if (document != null) {
      final PsiMetaData metaData = document.getMetaData();
      if (metaData instanceof XmlNSDescriptorImpl) {
        final XmlNSDescriptorImpl descriptor = (XmlNSDescriptorImpl) metaData;
        final String defaultNamespace = descriptor.getDefaultNamespace();
        if (StringUtils.isNotEmpty(defaultNamespace)) {
          XmlTag parentTag = xmlTag.getParentTag();
          if (parentTag != null) {
            XmlElementDescriptor[] descriptors = descriptor.getRootElementsDescriptors(PsiTreeUtil.getParentOfType(xmlTag, XmlDocument.class));
            for (XmlElementDescriptor xmlElementDescriptor: descriptors) {
              String prefix = schemaProvider.getPrefixFor(defaultNamespace);
              boolean couldContainDescriptor = couldContainDescriptor(parentTag, parentTag.getDescriptor(), xmlElementDescriptor, defaultNamespace);
              if (couldContainDescriptor) {
                XmlExtension.TagInfo tagInfo = createTagInfo(defaultNamespace, xmlElementDescriptor);
                LookupElementBuilder lookupElement = createLookupElement(tagInfo, defaultNamespace, prefix);
                result.addElement(lookupElement);
              }
            }
          }
        }
      }
    }
  }

  public static LookupElementBuilder createLookupElement(XmlExtension.TagInfo tagInfo,
                                                         final String tailText, @NotNull String namespacePrefix) {
    LookupElementBuilder builder =
        LookupElementBuilder.create(tagInfo, namespacePrefix + ":" + tagInfo.name).withInsertHandler(
            new ExtendedTagInsertHandler(tagInfo.name, tagInfo.namespace, namespacePrefix));
    if (!StringUtil.isEmpty(tailText)) {
      builder = builder.withTypeText(tailText, true);
    }
    return builder;
  }

  private XmlExtension.TagInfo createTagInfo(String defaultNamespace, XmlElementDescriptor xmlElementDescriptor) {
    return new XmlExtension.TagInfo(xmlElementDescriptor.getQualifiedName(), defaultNamespace) {
      @Nullable
      @Override
      public PsiElement getDeclaration() {
        return xmlElementDescriptor.getDeclaration();
      }
    };
  }

  static boolean couldContainDescriptor(final XmlTag parentTag,
                                        final XmlElementDescriptor parentDescriptor,
                                        final XmlElementDescriptor childDescriptor,
                                        String childNamespace) {

    if (XmlUtil.nsFromTemplateFramework(childNamespace)) return true;
    if (parentTag == null) return true;
    if (parentDescriptor == null) return false;
    final XmlTag childTag = parentTag.createChildTag(childDescriptor.getName(), childNamespace, null, false);
    childTag.putUserData(XmlElement.INCLUDING_ELEMENT, parentTag);
    final XmlElementDescriptor descriptor = parentDescriptor.getElementDescriptor(childTag, parentTag);
    return descriptor != null;
  }



}
