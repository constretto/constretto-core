/*
 * Copyright 2008 the original author or authors.
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
package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.provider.helper.ConfiguredUsingDefaults;
import org.constretto.internal.provider.helper.ConfiguredUsingListsAndMaps;
import org.constretto.internal.provider.helper.DataSourceConfiguration;
import org.constretto.internal.provider.helper.DataSourceConfigurationWithNatives;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.setProperty;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationAnnotationsTest {
    private ConstrettoConfiguration configuration;

    @Before
    public void prepareTests() {
        setProperty("url", "jdbc://url");
        setProperty("username", "username");
        setProperty("password", "password");
        setProperty("vendor", "derby");
        setProperty("version", "10");
        setProperty("derby.system.home", "C:\\home\\Derby\\");
        setProperty("array", "[\"one\",\"two\",\"three\"]");
        setProperty("map", "{\"1\":\"10\",\"2\":\"20\"}");
        configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();

    }

    @Test
    public void createNewAnnotatedConfigurationObject() {
        DataSourceConfiguration customerDataSource = configuration.as(DataSourceConfiguration.class);
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("password", customerDataSource.getPassword());
        assertEquals("derby", customerDataSource.getVendor());
        assertEquals(new Integer(10), customerDataSource.getVersion());
    }

    @Test
    public void applyConfigrationToAnnotatedConfigurationObject() {
        DataSourceConfiguration customerDataSource = new DataSourceConfiguration();
        configuration.on(customerDataSource);
        assertEquals("derby", customerDataSource.getVendor());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("password", customerDataSource.getPassword());
        assertEquals("C:\\home\\Derby\\", customerDataSource.homeDir);
        assertEquals(new Integer(10), customerDataSource.getVersion());
    }

    @Test
    public void applyConfigrationToAnnotatedConfigurationObjectUsingNativeTypes() {
        DataSourceConfigurationWithNatives customerDataSource = new DataSourceConfigurationWithNatives();
        configuration.on(customerDataSource);
        assertEquals(10, customerDataSource.getVersion());
        assertEquals(10, customerDataSource.getOtherVersion());
    }

    @Test
    public void applyConfigurationWithDefaultValues() {
        ConfiguredUsingDefaults configuredObject = new ConfiguredUsingDefaults();

        configuration.on(configuredObject);
        assertEquals("default-username", configuredObject.getStrangeUserName());
        assertEquals("default-password", configuredObject.getPassword());
        assertEquals("derby", configuredObject.getVendor());
        assertEquals(new Integer(Integer.MIN_VALUE), configuredObject.getVersion());
    }

    @Test
    public void listsShouldBeSupportedInAnnotations() {
        List<String> expectedList = new ArrayList<String>() {{
            add("one");
            add("two");
            add("three");
        }};
        Map<String, String> expectedMap = new HashMap<String, String>() {{
            put("1", "10");
            put("2", "20");
        }};
        ConfiguredUsingListsAndMaps configuredObject = new ConfiguredUsingListsAndMaps();
        configuration.on(configuredObject);
        assertList(expectedList,configuredObject.arrayFromField);
        assertList(expectedList,configuredObject.arrayFromMethod);
        assertMap(expectedMap,configuredObject.mapFromField);
        assertMap(expectedMap,configuredObject.mapFromMethod);
    }

    private void assertList(List<?> expected, List<?> result) {
        assertEquals(expected.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(expected.get(i), result.get(i));
        }
    }

    private void assertMap(Map<?, ?> expected, Map<?, ?> result) {
        assertEquals(expected.size(), result.size());
        for (Object key : expected.keySet()) {
            assertEquals(expected.get(key), result.get(key));
        }
    }
}
