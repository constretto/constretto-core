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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public abstract class Resource {
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILE_PREFIX = "file:";
    final String path;

    protected Resource(String path) {
        if (path == null) {
            throw new ConstrettoException("Resources with a null value for the 'path' argument is not allowed. ");
        }
        this.path = path;
    }


    public static Resource create(String path) {
        if (path.startsWith(CLASSPATH_PREFIX)) {
            return new ClassPathResource(path);
        } else if (path.startsWith(FILE_PREFIX)) {
            return new FileResource(path);
        } else {
            return new UrlResource(path);
        }
    }

    public abstract boolean exists();

    public abstract InputStream getInputStream();

}
