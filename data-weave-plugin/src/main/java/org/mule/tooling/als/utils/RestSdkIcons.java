package org.mule.tooling.als.utils;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class RestSdkIcons {

    public static final Icon RamlFileType = IconLoader.findIcon("icons/raml_type.png", RestSdkIcons.class.getClassLoader());
    public static final Icon OASFileType = IconLoader.findIcon("icons/openapi.png", RestSdkIcons.class.getClassLoader());

    private RestSdkIcons() {
        super();
    }
}