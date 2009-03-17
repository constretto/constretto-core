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
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.setProperty;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationNavigationTest {
    private ConstrettoConfiguration configuration;

    @Before
    public void prepareTests() {
        setProperty("datasources.customer.url", "jdbc://url");
        setProperty("datasources.customer.username", "username");
        setProperty("datasources.customer.password", "password");

        configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();

    }

    @Test
    public void evaluateInChildPosition() {
        ConstrettoConfiguration cursoredConfiguration = configuration.at("datasources").at("customer");
        assertEquals("jdbc://url", cursoredConfiguration.evaluateToString("url"));
        assertEquals("username", cursoredConfiguration.evaluateToString("username"));
        assertEquals("password", cursoredConfiguration.evaluateToString("password"));
        cursoredConfiguration = configuration.at("datasources").from("customer");
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
}