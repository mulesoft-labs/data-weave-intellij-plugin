package org.mule.tooling.lang.dw.util;

import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveInputDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;
import org.mule.tooling.lang.dw.parser.psi.WeaveOutputDirective;

import java.util.ArrayList;
import java.util.List;


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


    @NotNull
    public static List<WeaveInputDirective> getInputDirectiveList(@NotNull WeaveHeader header) {
        List<WeaveInputDirective> inputDirectiveList = new ArrayList<WeaveInputDirective>();
        for (WeaveDirective directive : header.getDirectiveList()) {
            if (directive instanceof WeaveInputDirective) {
                inputDirectiveList.add((WeaveInputDirective) directive);
            }
        }
        return inputDirectiveList;
    }

    @NotNull
    public static List<WeaveOutputDirective> getOutputDirectiveList(WeaveHeader header) {
        if (header == null) {
            return new ArrayList<>();
        }
        List<WeaveOutputDirective> outputDirectiveList = new ArrayList<WeaveOutputDirective>();
        for (WeaveDirective directive : header.getDirectiveList()) {
            if (directive instanceof WeaveOutputDirective) {
                outputDirectiveList.add((WeaveOutputDirective) directive);
            }
        }
        return outputDirectiveList;
    }

}
