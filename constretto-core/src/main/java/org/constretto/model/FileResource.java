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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class FileResource extends Resource {
    public FileResource(String path) {
        super(path);
    }

    @Override
    public boolean exists() {
        return new File(extractFileNameFromFileResource(path)).exists();
    }

    @Override
    public InputStream getInputStream() {
        String fileName = extractFileNameFromFileResource(path);
        try {
            return new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            throw new ConstrettoException("Could not read file from path: " + path);
        }
    }

    private String extractFileNameFromFileResource(String path) {
        String fileName;
        if (path.startsWith(FILE_PREFIX)) {
            fileName = decode(path.substring(FILE_PREFIX.length(), path.length()));
        } else {
            fileName = path;
        }
        return fileName;
    }

    private String decode(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return path;
    }

}
