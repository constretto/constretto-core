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

import org.junit.Test;

import java.io.File;

import static org.constretto.internal.converter.ValueConverterRegistry.convert;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class FileConverterTest {

    @Test
    public void simpleFileConversion() {
        File file = new File(".");
        File convertedFile = convert(File.class, ".");
        assertEquals(file, convertedFile);
        assertTrue(convertedFile.isDirectory());
        assertTrue(convertedFile.canRead());
    }


    @Test
    public void fileConversionWithBaseDir() {
        File file = new File("./pom.xml");
        File convertedFile = new FileValueConverter(new File(".")).fromString("pom.xml");
        assertEquals(file, convertedFile);
        assertFalse(convertedFile.isDirectory());
        assertTrue(convertedFile.canRead());
    }

    @Test
    public void fileConversionWithBaseDirAndAbsoluteFile() {
        File file = new File("./pom.xml").getAbsoluteFile();
        File convertedFile = new FileValueConverter(new File("."), true).fromString("pom.xml");
        assertEquals(file, convertedFile);
        assertFalse(convertedFile.isDirectory());
        assertTrue(convertedFile.canRead());
    }
}
