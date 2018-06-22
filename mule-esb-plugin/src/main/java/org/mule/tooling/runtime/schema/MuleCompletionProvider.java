package org.mule.tooling.runtime.schema;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.XmlCompletionContributor;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
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
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import com.intellij.xml.impl.schema.XmlNSDescriptorImpl;
import com.intellij.xml.util.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MuleCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiElement parent = parameters.getPosition().getParent();
    if (parent instanceof XmlTag) {
      final XmlTag xmlTag = (XmlTag) parent;
      boolean intellijIdeaRulezzz = xmlTag.getLocalName().equalsIgnoreCase("IntellijIdeaRulezzz");
      final Module module = ModuleUtil.findModuleForFile(parameters.getOriginalFile());
      if (intellijIdeaRulezzz && module != null) {
        final String namespacePrefix = xmlTag.getNamespacePrefix();
        final Project project = xmlTag.getProject();
        final MuleModuleSchemaProvider schemaProvider = MuleModuleSchemaProvider.getInstance(module);
        final Optional<SchemaInformation> schemaFromPrefix = schemaProvider.getSchemaFromPrefix(namespacePrefix);
        if (schemaFromPrefix.isPresent()) {
          final SchemaInformation schemaInformation = schemaFromPrefix.get();
          final Optional<XmlFile> schemaAsXmlFile = schemaInformation.getSchemaAsXmlFile(project);
          if (schemaAsXmlFile.isPresent()) {
            XmlFile file = schemaAsXmlFile.get();
            addPossibleTagsFrom(file, xmlTag, schemaProvider, result);
          }
        }
      }
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
              LookupElement lookupElement = XmlCompletionContributor.createLookupElement(createTagInfo(defaultNamespace, xmlElementDescriptor), defaultNamespace, schemaProvider.getPrefixFor(defaultNamespace));
              result.addElement(lookupElement);
            }
          }
        }
      }
    }
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
                                        String childNamespace, boolean strict) {

    if (XmlUtil.nsFromTemplateFramework(childNamespace)) return true;
    if (parentTag == null) return true;
    if (parentDescriptor == null) return false;
    final XmlTag childTag = parentTag.createChildTag(childDescriptor.getName(), childNamespace, null, false);
    childTag.putUserData(XmlElement.INCLUDING_ELEMENT, parentTag);
    XmlElementDescriptor descriptor = parentDescriptor.getElementDescriptor(childTag, parentTag);
    return descriptor != null && (!strict || !(descriptor instanceof AnyXmlElementDescriptor));
  }


//  private static void processVariantsInNamespace(final String namespace,
//                                                 final XmlTag element,
//                                                 final List<XmlElementDescriptor> variants,
//                                                 final XmlElementDescriptor elementDescriptor,
//                                                 final String elementNamespace,
//                                                 final Map<String, XmlElementDescriptor> descriptorsMap,
//                                                 final Set<XmlNSDescriptor> visited,
//                                                 XmlTag parent,
//                                                 final XmlExtension extension) {
//    if (descriptorsMap.containsKey(namespace)) {
//      final XmlElementDescriptor descriptor = descriptorsMap.get(namespace);
//
//      if (isAcceptableNs(element, elementDescriptor, elementNamespace, namespace)) {
//        for (XmlElementDescriptor containedDescriptor: descriptor.getElementsDescriptors(parent)) {
//          if (containedDescriptor != null) variants.add(containedDescriptor);
//        }
//      }
//
//      if (element instanceof HtmlTag) {
//        HtmlUtil.addHtmlSpecificCompletions(descriptor, element, variants);
//      }
//      visited.add(descriptor.getNSDescriptor());
//    } else {
//      // Don't use default namespace in case there are other namespaces in scope
//      // If there are tags from default namespace they will be handled via
//      // their element descriptors (prev if section)
//      if (namespace == null) return;
//      if (namespace.isEmpty() && !visited.isEmpty()) return;
//
//      XmlNSDescriptor nsDescriptor = getDescriptor(element, namespace, true, extension);
//      if (nsDescriptor == null) {
//        if (!descriptorsMap.isEmpty()) return;
//        nsDescriptor = getDescriptor(element, namespace, false, extension);
//      }
//
//      if (nsDescriptor != null && !visited.contains(nsDescriptor) &&
//          isAcceptableNs(element, elementDescriptor, elementNamespace, namespace)
//      ) {
//        visited.add(nsDescriptor);
//        final XmlElementDescriptor[] rootElementsDescriptors =
//            nsDescriptor.getRootElementsDescriptors(PsiTreeUtil.getParentOfType(element, XmlDocument.class));
//
//        final XmlTag parentTag = extension.getParentTagForNamespace(element, nsDescriptor);
//        final XmlElementDescriptor parentDescriptor;
//        if (parentTag == element.getParentTag()) {
//          parentDescriptor = elementDescriptor;
//        } else {
//          assert parentTag != null;
//          parentDescriptor = parentTag.getDescriptor();
//        }
//
//        for (XmlElementDescriptor candidateDescriptor: rootElementsDescriptors) {
//          if (candidateDescriptor != null &&
//              couldContainDescriptor(parentTag, parentDescriptor, candidateDescriptor, namespace, false)) {
//            variants.add(candidateDescriptor);
//          }
//        }
//      }
//    }
//  }


}
