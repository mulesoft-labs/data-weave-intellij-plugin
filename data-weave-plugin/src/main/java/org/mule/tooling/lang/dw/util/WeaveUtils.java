package org.mule.tooling.lang.dw.util;

import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;

public class WeaveUtils {

    public static boolean isTestFile(WeaveDocument document) {
        if (document == null)
            return false;
        WeaveHeader header = document.getHeader();
        if (header != null) {
            return header.getDirectiveList().stream().anyMatch((directive) -> {
                if (directive instanceof WeaveImportDirective) {
                    WeaveModuleReference moduleReference = ((WeaveImportDirective) directive).getModuleReference();
                    if (moduleReference != null) {
                        String moduleFQN = moduleReference.getModuleFQN();
                        return moduleFQN.equals("dw::test::Tests");
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            });
        }
        return false;
    }

}
