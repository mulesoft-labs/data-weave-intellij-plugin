package org.mule.tooling.als.component;

import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;

public class ALSLanguageExtensionService {

  private static final ExtensionPointName<ALSLanguageExtension> EP_NAME =
          ExtensionPointName.create("org.mule.tooling.intellij.dataweave.v2.alsLanguage");

  public static ALSLanguageExtension @NotNull [] languages() {
    return EP_NAME.getExtensions();
  }
}
