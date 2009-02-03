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

import static java.lang.System.setProperty;
import static junit.framework.Assert.assertEquals;

import org.constretto.Constretto;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.provider.helper.DataSourceConfiguration;
import org.constretto.internal.store.SystemPropertiesStore;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationProviderTest {
    private ConstrettoConfiguration configuration;

    @Before
    public void prepareTests() {
        setProperty("datasources.customer.url", "jdbc://url");
        setProperty("datasources.customer.username", "username");
        setProperty("datasources.customer.password", "password");

        configuration = new Constretto().addConfigurationStore(new SystemPropertiesStore()).done().getConfiguration();

    }

    @Test
    public void evaluateInChildPosition() {
        ConstrettoConfiguration cursoredConfiguration = configuration.at("datasources").at("customer");
        assertEquals("jdbc://url", cursoredConfiguration.evaluateToString("url"));
        assertEquals("username", cursoredConfiguration.evaluateToString("username"));
        assertEquals("password", cursoredConfiguration.evaluateToString("password"));
    }

    @Test
    public void evaluateWithExpression() {
        assertEquals("jdbc://url", configuration.evaluateToString("datasources.customer.url"));
        assertEquals("username", configuration.evaluateToString("datasources.customer.username"));
        assertEquals("password", configuration.evaluateToString("datasources.customer.password"));
    }

    @Test
    public void createNewConfigurationObject() {
        DataSourceConfiguration customerDataSource = configuration.at("datasources.customer").as(DataSourceConfiguration.class);
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("password", customerDataSource.getPassword());
    }

    @Test
    public void applyConfigurationObject() {
        DataSourceConfiguration customerDataSource = new DataSourceConfiguration();
        configuration.at("datasources.customer").applyOn(customerDataSource);
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("password", customerDataSource.getPassword());
    }
}
