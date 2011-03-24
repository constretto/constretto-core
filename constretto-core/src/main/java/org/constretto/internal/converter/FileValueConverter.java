/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.internal.converter;

import org.constretto.exception.ConstrettoConversionException;

import java.io.File;

/**
 * @author trygvis
 */
public class FileValueConverter implements ValueConverter<File> {

    private final File basedir;
    private final boolean convertToAbsolute;

    public FileValueConverter() {
        this(null);
    }

    public FileValueConverter(File basedir) {
        this(basedir, false);
    }

    public FileValueConverter(File basedir, boolean convertToAbsolute) {
        this.basedir = basedir;
        this.convertToAbsolute = convertToAbsolute;
    }

    public File fromString(String value) throws ConstrettoConversionException {
        File f = new File(value);

        if (basedir == null) {
            return convertToAbsolute(f);
        }

        if (f.isAbsolute()) {
            return f;
        }

        return convertToAbsolute(new File(basedir, value));
    }

    private File convertToAbsolute(File f) {
        return convertToAbsolute ? f.getAbsoluteFile() : f;
    }
}