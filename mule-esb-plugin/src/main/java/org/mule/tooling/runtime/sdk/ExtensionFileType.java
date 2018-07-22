package org.mule.tooling.runtime.sdk;

import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ExtensionFileType extends LanguageFileType {

    @NonNls
    public static final String DEFAULT_EXTENSION = "java";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = ".java";
    public static final ExtensionFileType INSTANCE = new ExtensionFileType();

    private ExtensionFileType() {
        super(JavaLanguage.INSTANCE);
    }

    @Override
    @NotNull
    public String getName() {
        return "JAVA";
    }

    @Override
    @NotNull
    public String getDescription() {
        return IdeBundle.message("filetype.description.java");
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Java;
    }

    @Override
    public boolean isJVMDebuggingSupported() {
        return true;
    }
}

