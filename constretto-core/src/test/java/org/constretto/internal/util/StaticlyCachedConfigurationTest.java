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
package org.constretto.internal.util;

import org.constretto.exception.ConstrettoExpressionException;
import org.junit.Before;
import org.junit.Test;

import static org.constretto.util.StaticlyCachedConfiguration.*;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class StaticlyCachedConfigurationTest {

    @Before
    public void setUp() throws Exception {
        reset();
    }

    @Test
    public void cachedSingleIniFile() {
        String value = config("classpath:cache1.ini").evaluateToString("key1");
        assertEquals("value1", value);
        assertEquals(0, cacheHits());
        assertEquals(1, cacheMiss());
        value = config("classpath:cache1.ini").evaluateToString("key1");
        assertEquals("value1", value);
        assertEquals(1, cacheHits());
        assertEquals(1, cacheMiss());
    }

    @Test
    public void cachedMultipleIniFile() {
        String value = config("classpath:cache1.ini", "classpath:cache2.ini").evaluateToString("key1");
        assertEquals("value1", value);
        String value2 = config("classpath:cache1.ini", "classpath:cache2.ini").evaluateToString("key2");
        assertEquals("value2", value2);
    }

    @Test
    public void cachedMultipleFilesWithDifferentType() {
        String value = config("classpath:cache1.ini", "classpath:cache2.ini", "classpath:cache3.properties").evaluateToString("key1");
        assertEquals("value1", value);
        String value2 = config("classpath:cache1.ini", "classpath:cache2.ini", "classpath:cache3.properties").evaluateToString("key2");
        assertEquals("value2", value2);
        String value3 = config("classpath:cache1.ini", "classpath:cache2.ini", "classpath:cache3.properties").evaluateToString("key3");
        assertEquals("value3", value3);
    }

    @Test
    public void cachedSinglePropertyFile() {
        String value = config("classpath:cache3.properties").evaluateToString("key3");
        assertEquals("value3", value);
    }

    @Test(expected = ConstrettoExpressionException.class)
    public void withoutSystemProperties() {
        System.setProperty("key1", "sys-prop1");
        try {
            config(false, "classpath:cache2.ini").evaluateToString("key1");
        } finally {
            System.clearProperty("key1");
        }
    }

    @Test
    public void withSystemProperties() throws Exception {
        final String systemPropertyValue = "sys-prop1";
        System.setProperty("key1", systemPropertyValue);
        assertEquals(systemPropertyValue, config(true, "classpath:cache2.ini").evaluateToString("key1"));
        System.clearProperty("key1");

    }
}
