/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.constretto.model;

import org.constretto.exception.ConstrettoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class Resource {
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILE_PREFIX = "file:";
    private final InputStream inputStream;


    public Resource(String path) {
        inputStream = loadResource(path);
    }

    public boolean exists() {
        return inputStream != null;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private InputStream loadResource(String path) {
        if (path.startsWith(CLASSPATH_PREFIX) || this instanceof ClassPathResource) {
            return loadFromClassPath(path);
        } else if (path.startsWith(FILE_PREFIX) || this instanceof FileResource) {
            return loadFromFile(path);
        } else {
            try {
                URL url = new URL(path);
                return loadFromURL(url);
            } catch (MalformedURLException ex) {
                throw new ConstrettoException("Unsupported resource type. Registered types are 'classpath:', 'file:', and things that can be loaded with java.net.URL");
            }
        }
    }

    private InputStream loadFromURL(URL url) {
        try {
            return new FileInputStream(url.getFile());
        } catch (FileNotFoundException e) {
            throw new ConstrettoException("Could not load resource: " + url.toString(), e);
        }
    }

    private InputStream loadFromFile(String path) {
        String fileName;
        if (path.startsWith(FILE_PREFIX)) {
            fileName = path.substring(FILE_PREFIX.length(), path.length());
        } else {
            fileName = path;
        }
        try {
            return new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            throw new ConstrettoException("Unsupported resource type. Registered types are 'classpath:', 'file:', and things that can be loaded with java.net.URL");
        }


    }

    private InputStream loadFromClassPath(String path) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String location;
        if (path.startsWith(CLASSPATH_PREFIX)) {
            location = path.substring(CLASSPATH_PREFIX.length(), path.length());
        } else {
            location = path;
        }
        return classLoader.getResourceAsStream(location);
    }
}
