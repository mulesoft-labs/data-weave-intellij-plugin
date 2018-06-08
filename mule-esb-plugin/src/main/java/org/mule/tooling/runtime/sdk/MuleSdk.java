/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mule.tooling.runtime.sdk;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class MuleSdk {

    private static final String BOOT_DIR = "/lib/boot";
    private static final String PATCH_DIR = "/lib/patches";
    private static final String MULE_DIR = "/lib/mule";
    private static final String USER_DIR = "/lib/user";
    private static final String OPT_DIR = "/lib/opt";
    private static final String ENDORSED_DIR = "/lib/endorsed";

    public static final String UNDEFINED_VERSION = "0.0.0";

    private static List<String> MULE_REQUIRED_FOLDERS = Arrays.asList(BOOT_DIR, MULE_DIR, USER_DIR, OPT_DIR);
    private static final String BIN_DIR = "/bin";
    private static final Logger LOG = Logger.getInstance("#com.intellij.appengine.sdk.impl.MuleSdk");
    private static final Pattern VERSION_NUMBER = Pattern.compile("([0-9]\\.[0-9]\\.[0-9])");

    @Tag("mule-home")
    private String muleHome;

    public MuleSdk() {
    }

    public MuleSdk(String homePath) {
        this.muleHome = homePath;
    }

    public String getMuleHome() {
        return muleHome;
    }

    public void setMuleHome(String muleHome) {
        this.muleHome = muleHome;
    }

    @NotNull
    public String getVersion() {
        final File file = new File(getMuleHome());
        final String distroName = file.getName();
        return distroName.substring("mule-enterprise-standalone-".length());
    }


    @Override
    public String toString() {
        return getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MuleSdk muleSdk = (MuleSdk) o;

        return muleHome != null ? muleHome.equals(muleSdk.muleHome) : muleSdk.muleHome == null;

    }

    @Override
    public int hashCode() {
        return muleHome != null ? muleHome.hashCode() : 0;
    }

    //Helper methods
    public static boolean isValidMuleHome(String dir) {
        if (dir == null) {
            return false;
        }
        final File muleHome = new File(dir);
        for (String muleJarsFolder : MULE_REQUIRED_FOLDERS) {
            if (!new File(muleHome, muleJarsFolder).exists()) {
                return false;
            }
        }
        //Check bin directory
        return new File(muleHome, BIN_DIR).exists();
    }

}
