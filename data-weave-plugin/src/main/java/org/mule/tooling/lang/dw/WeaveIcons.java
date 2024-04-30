package org.mule.tooling.lang.dw;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class WeaveIcons {

    public static final Icon WeaveFileType = IconLoader.getIcon("/icons/weave_file_type.png", WeaveIcons.class.getClassLoader());
    public static final Icon WeaveTestFileType = IconLoader.getIcon("/icons/weave_test_file_type.png", WeaveIcons.class.getClassLoader());

    public static final Icon Bat = IconLoader.getIcon("/icons/bat.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveIcon = IconLoader.getIcon("/icons/dataweave.png", WeaveIcons.class.getClassLoader());
    public static final Icon RestSdkIcon = IconLoader.getIcon("/icons/rest_sdk_module.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveModuleIcon = IconLoader.getIcon("/icons/dw-module-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveMappingIcon = IconLoader.getIcon("/icons/dw-mapping-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveTestIcon = IconLoader.getIcon("/icons/dw-module-icon.png", WeaveIcons.class.getClassLoader());
    public static final Icon DataWeaveTestingFrameworkIcon = IconLoader.getIcon("/icons/dw-module-icon.png", WeaveIcons.class.getClassLoader());

    private WeaveIcons() {
        super();
    }

}