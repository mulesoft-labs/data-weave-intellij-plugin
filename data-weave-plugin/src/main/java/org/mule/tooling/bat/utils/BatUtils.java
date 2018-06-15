package org.mule.tooling.bat.utils;

import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;

public class BatUtils {

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
            return moduleFQN.equals("bat::BDD");
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
