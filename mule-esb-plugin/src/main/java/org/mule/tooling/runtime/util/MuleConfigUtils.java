package org.mule.tooling.runtime.util;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

public class MuleConfigUtils {
    public static final String CONFIG_RELATIVE_PATH = "src/main/app";

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

    public static boolean isExceptionStrategyTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(EXCEPTION_STRATEGY_LOCAL_NAME) ||
                rootTag.getLocalName().equalsIgnoreCase(CHOICE_EXCEPTION_STRATEGY_LOCAL_NAME) ||
                rootTag.getLocalName().equalsIgnoreCase(CATCH_EXCEPTION_STRATEGY_LOCAL_NAME) ||
                rootTag.getLocalName().equalsIgnoreCase(ROLLBACK_EXCEPTION_STRATEGY_LOCAL_NAME);
    }

    public static boolean isSubFlowTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MULE_SUB_FLOW_LOCAL_NAME);
    }

    public static boolean isMUnitTestTag(XmlTag rootTag) {
        return rootTag.getLocalName().equalsIgnoreCase(MUNIT_TEST_LOCAL_NAME);
    }

    public static boolean isTopLevelTag(XmlTag tag) {
        return isFlowTag(tag) || isSubFlowTag(tag) || isMUnitTestTag(tag) || isExceptionStrategyTag(tag);
    }


}
