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

import static junit.framework.Assert.assertEquals;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.provider.helper.ConfiguredUsingDefaults;
import org.constretto.internal.provider.helper.DataSourceConfiguration;
import org.constretto.internal.provider.helper.DataSourceConfigurationWithNatives;
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.setProperty;

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
}
