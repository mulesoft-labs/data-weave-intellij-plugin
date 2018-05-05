package org.mule.tooling.lang.dw.regex;

import org.intellij.lang.regexp.DefaultRegExpPropertiesProvider;
import org.intellij.lang.regexp.RegExpLanguageHost;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeaveRegExpLanguageHost implements RegExpLanguageHost {
    @Override
    public boolean characterNeedsEscaping(char c) {
        return false;
    }

    @Override
    public boolean supportsPerl5EmbeddedComments() {
        return false;
    }

    @Override
    public boolean supportsPossessiveQuantifiers() {
        return true;
    }

    @Override
    public boolean supportsPythonConditionalRefs() {
        return false;
    }

    @Override
    public boolean supportsNamedGroupSyntax(RegExpGroup group) {
        return true;
    }

    @Override
    public boolean supportsNamedGroupRefSyntax(RegExpNamedGroupRef ref) {
        return true;
    }

    @Override
    public boolean supportsExtendedHexCharacter(RegExpChar regExpChar) {
        return false;
    }

    @Override
    public boolean isValidCategory(@NotNull String category) {
        if (category.startsWith("Is")) {
            try {
                return Character.UnicodeBlock.forName(category.substring(2)) != null;
            }
            catch (IllegalArgumentException ignore) {}
        }
        for (String[] name : DefaultRegExpPropertiesProvider.getInstance().getAllKnownProperties()) {
            if (name[0].equals(category)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public String[][] getAllKnownProperties() {
        return DefaultRegExpPropertiesProvider.getInstance().getAllKnownProperties();
    }

    @Nullable
    @Override
    public String getPropertyDescription(@Nullable String name) {
        return DefaultRegExpPropertiesProvider.getInstance().getPropertyDescription(name);
    }

    @NotNull
    @Override
    public String[][] getKnownCharacterClasses() {
        return DefaultRegExpPropertiesProvider.getInstance().getKnownCharacterClasses();
    }
}
