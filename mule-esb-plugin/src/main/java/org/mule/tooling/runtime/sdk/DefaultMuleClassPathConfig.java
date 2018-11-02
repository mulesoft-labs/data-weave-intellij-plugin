/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.runtime.sdk;

import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Constructs a default set of JAR Urls located under Mule home folder.
 */
public class DefaultMuleClassPathConfig {

    protected static final String BOOT_DIR = "/lib/boot";
    protected static final String MULE_DIR = "/lib/mule";
    protected static final String USER_DIR = "/lib/user";
    protected static final String OPT_DIR = "/lib/opt";

    protected List<File> urls = new ArrayList<>();

    public DefaultMuleClassPathConfig(File muleHome, File muleBase) {
        init(muleHome, muleBase);
    }

    protected void init(File muleHome, File muleBase) {
        /*
         * Pick up any local jars, if there are any. Doing this here insures that any local class that override the global classes
         * will in fact do so.
         */
        addMuleBaseUserLibs(muleHome, muleBase);

        addLibraryDirectory(muleHome, BOOT_DIR);
        addLibraryDirectory(muleHome, USER_DIR);
        addLibraryDirectory(muleHome, MULE_DIR);
        addLibraryDirectory(muleHome, OPT_DIR);
    }

    protected void addMuleBaseUserLibs(File muleHome, File muleBase) {
        try {
            if (!FileUtil.filesEqual(muleHome.getCanonicalFile(), muleBase.getCanonicalFile())) {
                File userOverrideDir = new File(muleBase, USER_DIR);
                addFile(userOverrideDir);
                addFiles(listJars(userOverrideDir));
            }
        } catch (IOException ioe) {
            System.out.println("Unable to check to see if there are local jars to load: " + ioe.toString());
        }
    }

    protected void addLibraryDirectory(File muleHome, String libDirectory) {
        File directory = new File(muleHome, libDirectory);
        addFile(directory);
        addFiles(listJars(directory));
    }

    public List<File> getURLs() {
        return new ArrayList<File>(this.urls);
    }


    /**
     * Add a URL to Mule's classpath.
     *
     * @param url folder (should end with a slash) or jar path
     */
    public void addURL(File url) {
        this.urls.add(url);
    }

    public void addFiles(List<File> files) {
        for (Iterator<File> i = files.iterator(); i.hasNext(); ) {
            this.addFile(i.next());
        }
    }

    public void addFile(File jar) {
        this.addURL(jar.getAbsoluteFile());
    }

    /**
     * Find and if necessary filter the jars for classpath.
     *
     * @return a list of {@link java.io.File}s
     */
    protected List<File> listJars(File path) {
        File[] jars = path.listFiles(pathname -> {
            try {
                return pathname.getCanonicalPath().endsWith(".jar");
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        if (jars != null) {
            return Arrays.asList(jars);
        }
        return Collections.emptyList();
    }

}