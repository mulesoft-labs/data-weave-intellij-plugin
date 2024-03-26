package org.mule.tooling.lang.dw;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class WeaveIcons {

    public static final Icon WeaveFileType = IconLoader.findIcon("/icons/weave_file_type.png", WeaveIcons.class.getClassLoader());
    public static final Icon WeaveTestFileType = IconLoader.findIcon("/icons/weave_test_file_type.png", WeaveIcons.class.getClassLoader());

    public static final Icon Bat = IconLoader.findIcon("/icons/bat.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveIcon = IconLoader.findIcon("/icons/dataweave.png", WeaveIcons.class.getClassLoader());
    public static final Icon RestSdkIcon = IconLoader.findIcon("/icons/rest_sdk_module.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveModuleIcon = IconLoader.findIcon("/icons/dw-module-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveMappingIcon = IconLoader.findIcon("/icons/dw-mapping-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveTestIcon = IconLoader.findIcon("/icons/dw-test-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveTestingFrameworkIcon = IconLoader.findIcon("/icons/dw-testing-framework.png", WeaveIcons.class.getClassLoader());

    private WeaveIcons() {
        super();
    }

}