/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.kvasir.eclipse;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;


/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectClassLoader extends URLClassLoader
{

    private String[] errorURL;


    public ProjectClassLoader(IJavaProject project)
    {
        super(new URL[0]);
        getClassPaths(project);
    }


    private void getClassPaths(IJavaProject project)
    {
        if (project == null) {
            throw new NullPointerException();
        }
        List errorList = new ArrayList();
        try {
            IClasspathEntry[] entries = project.getResolvedClasspath(true);
            for (int i = 0; i < entries.length; i++) {
                int kind = entries[i].getEntryKind();
                String url = null;
                if (kind == IClasspathEntry.CPE_SOURCE) {
                    try {
                        url = "file:///"
                            + entries[i].getOutputLocation().toString();
                    } catch (RuntimeException e1) {
                        continue;
                    }
                } else {
                    url = "jar:file:///" + entries[i].getPath().toString()
                        + "!/";
                }
                try {
                    addURL(new URL(url));
                } catch (MalformedURLException e) {
                    errorList.add(url);
                }
            }
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }


    public String[] getErrorPath()
    {
        if (errorURL == null) {
            return new String[0];
        }
        return errorURL;
    }
}
