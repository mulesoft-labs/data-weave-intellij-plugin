package org.mule.tooling.runtime.util;

import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.pom.NonNavigatable;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.impl.schema.ComplexTypeDescriptor;
import com.intellij.xml.impl.schema.TypeDescriptor;
import com.intellij.xml.impl.schema.XmlElementDescriptorImpl;
//import com.mulesoft.mule.debugger.commons.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//import org.mule.tooling.esb.config.MuleConfigConstants;
//import org.mule.tooling.esb.config.model.Flow;
//import org.mule.tooling.esb.config.model.Mule;
//import org.mule.tooling.esb.config.model.SubFlow;
//import org.mule.tooling.esb.framework.facet.MuleFacet;
//import org.mule.tooling.esb.framework.facet.MuleFacetConfiguration;
//import org.mule.tooling.esb.framework.facet.MuleFacetType;
//import org.mule.tooling.esb.sdk.MuleSdk;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;

import javax.xml.namespace.QName;
import java.util.*;

public class MuleConfigUtils {

    public static final String MULE_LOCAL_NAME = "mule";
    public static final String MULE_FLOW_LOCAL_NAME = "flow";
    public static final String MULE_SUB_FLOW_LOCAL_NAME = "sub-flow";

    public static final String MUNIT_TEST_LOCAL_NAME = "test";
    public static final String MUNIT_NAMESPACE = "munit";

    public static final String EXCEPTION_STRATEGY_LOCAL_NAME = "exception-strategy";
    public static final String CHOICE_EXCEPTION_STRATEGY_LOCAL_NAME = "choice-exception-strategy";
    public static final String ROLLBACK_EXCEPTION_STRATEGY_LOCAL_NAME = "rollback-exception-strategy";
    public static final String CATCH_EXCEPTION_STRATEGY_LOCAL_NAME = "catch-exception-strategy";

    public static boolean isMuleFile(PsiFile psiFile) {
        if (!(psiFile instanceof XmlFile)) {
            return false;
        }
        if (psiFile.getFileType() != StdFileTypes.XML) {
            return false;
        }
        final XmlFile psiFile1 = (XmlFile) psiFile;
        final XmlTag rootTag = psiFile1.getRootTag();
        return isMuleTag(rootTag);
    }

    public static boolean isMUnitFile(PsiFile psiFile) {
        if (!(psiFile instanceof XmlFile)) {
            return false;
        }
        if (psiFile.getFileType() != StdFileTypes.XML) {
            return false;
        }
        final XmlFile psiFile1 = (XmlFile) psiFile;
        final XmlTag rootTag = psiFile1.getRootTag();
        if (rootTag == null || !isMuleTag(rootTag)) {
            return false;
        }
        final XmlTag[] munitTags = rootTag.findSubTags(MUNIT_TEST_LOCAL_NAME, rootTag.getNamespaceByPrefix(MUNIT_NAMESPACE));
        return munitTags.length > 0;
    }

    public static boolean isMuleTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MULE_LOCAL_NAME);
    }

    public static boolean isFlowTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MULE_FLOW_LOCAL_NAME);
    }

    public static boolean isSubFlowTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MULE_SUB_FLOW_LOCAL_NAME);
    }

    public static boolean isMUnitTestTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MUNIT_TEST_LOCAL_NAME);
    }

    public static boolean isTopLevelTag(XmlTag tag) {
//        return isFlowTag(tag) || isSubFlowTag(tag) || isMUnitTestTag(tag) || isExceptionStrategyTag(tag);
        return isFlowTag(tag) || isSubFlowTag(tag) || isMUnitTestTag(tag);
    }

    public static boolean isInTopLevelTag(XmlTag tag) {
        boolean inTopLevel = false;
        XmlTag current = tag;

        while (!inTopLevel && current != null) {
            inTopLevel = MuleConfigUtils.isTopLevelTag(current);
            if (!inTopLevel)
                current = current.getParentTag();
        }

        return inTopLevel;
    }

    public static QName getQName(XmlTag xmlTag) {
        return new QName(xmlTag.getNamespace(), xmlTag.getLocalName());
    }

    @Nullable
    public static XSourcePosition createPositionByElement(PsiElement element) {
        if (element == null)
            return null;

        PsiFile psiFile = element.getContainingFile();
        if (psiFile == null)
            return null;

        final VirtualFile file = psiFile.getVirtualFile();
        if (file == null)
            return null;

        final SmartPsiElementPointer<PsiElement> pointer =
                SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);

        return new XSourcePosition() {
            private volatile XSourcePosition myDelegate;

            private XSourcePosition getDelegate() {
                if (myDelegate == null) {
                    myDelegate = ApplicationManager.getApplication().runReadAction(new Computable<XSourcePosition>() {
                        @Override
                        public XSourcePosition compute() {
                            PsiElement elem = pointer.getElement();
                            return XSourcePositionImpl.createByOffset(pointer.getVirtualFile(), elem != null ? elem.getTextOffset() : -1);
                        }
                    });
                }
                return myDelegate;
            }

            @Override
            public int getLine() {
                return getDelegate().getLine();
            }

            @Override
            public int getOffset() {
                return getDelegate().getOffset();
            }

            @NotNull
            @Override
            public VirtualFile getFile() {
                return file;
            }

            @NotNull
            @Override
            public Navigatable createNavigatable(@NotNull Project project) {
                // no need to create delegate here, it may be expensive
                if (myDelegate != null) {
                    return myDelegate.createNavigatable(project);
                }
                PsiElement elem = pointer.getElement();
                if (elem instanceof Navigatable) {
                    return ((Navigatable) elem);
                }
                return NonNavigatable.INSTANCE;
            }
        };
    }

    @NotNull
    private static String getPrefix(XmlTag weavePart) {
        final String localName = weavePart.getLocalName();
        if (localName.equals("set-payload")) {
            return "payload:";
        } else if (localName.equals("set-variable")) {
            return "flowVar:" + weavePart.getAttributeValue("variableName");
        } else if (localName.equals("set-property")) {
            return "property:" + weavePart.getAttributeValue("propertyName");
        } else if (localName.equals("set-session-variable")) {
            return "sessionVar:" + weavePart.getAttributeValue("variableName");
        }

        return "payload:";
    }

    @Nullable
    public static XmlTag getXmlTagAt(Project project, XSourcePosition sourcePosition) {
        final VirtualFile file = sourcePosition.getFile();
        final XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(file);
        final XmlTag rootTag = xmlFile.getRootTag();
        return findXmlTag(sourcePosition, rootTag);
    }

    private static XmlTag findXmlTag(XSourcePosition sourcePosition, XmlTag rootTag) {
        final XmlTag[] subTags = rootTag.getSubTags();
        for (int i = 0; i < subTags.length; i++) {
            XmlTag subTag = subTags[i];
            final int subTagLineNumber = getLineNumber(sourcePosition.getFile(), subTag);
            if (subTagLineNumber == sourcePosition.getLine()) {
                return subTag;
            } else if (subTagLineNumber > sourcePosition.getLine() && i > 0 && subTags[i - 1].getSubTags().length > 0) {
                return findXmlTag(sourcePosition, subTags[i - 1]);
            }
        }
        if (subTags.length > 0) {
            final XmlTag lastElement = subTags[subTags.length - 1];
            return findXmlTag(sourcePosition, lastElement);
        } else {
            return null;
        }
    }

    public static int getLineNumber(VirtualFile file, XmlTag tag) {
        final int offset = tag.getTextOffset();
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        return offset < document.getTextLength() ? document.getLineNumber(offset) : -1;
    }

    @NotNull
    public static String asMelScript(@NotNull String script) {
        return !script.startsWith("#[") ? "#[" + script + "]" : script;
    }

    public static boolean isGlobalElement(XmlTag subTag) {
        return !(subTag.getName().equals("flow") || subTag.getName().equals("sub-flow") || subTag.getLocalName().equals("test"));
    }

    @Nullable
    public static XmlTag findParentXmlTag(PsiElement element) {
        PsiElement psiElement = element;

        while (psiElement != null && !(psiElement instanceof XmlTag))
            psiElement = psiElement.getParent();

        return (XmlTag) psiElement;
    }

}
