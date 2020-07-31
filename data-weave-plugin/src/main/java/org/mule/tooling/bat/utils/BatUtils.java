package org.mule.tooling.bat.utils;

import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveFqnIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;

public class BatUtils {

  public static boolean isTestFile(WeaveDocument document) {
    if (document == null)
      return false;
    WeaveHeader header = document.getHeader();
    if (header != null) {
      return header.getDirectiveList().stream().anyMatch((directive) -> {
        if (directive instanceof WeaveImportDirective) {
          WeaveFqnIdentifier moduleReference = ((WeaveImportDirective) directive).getFqnIdentifier();
          if (moduleReference != null) {
            String moduleFQN = moduleReference.getModuleFQN();
            return moduleFQN.equals("bat::BDD") || moduleFQN.equals("bat::Core");
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
