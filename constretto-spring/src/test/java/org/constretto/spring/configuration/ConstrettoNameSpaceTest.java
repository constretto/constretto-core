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
package org.constretto.spring.configuration;

import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConstrettoNameSpaceTest {

    @Autowired
    private ConstrettoConfiguration configuration;

    @BeforeClass
    public static void setupTags() {
        System.setProperty(DefaultConfigurationContextResolver.TAGS, "test");
        System.clearProperty("key7");
    }

    @AfterClass
    public static void clearTags() {
        System.clearProperty(DefaultConfigurationContextResolver.TAGS);
    }


    @Test
    public void namespaceConfiguredContext() {
        Assert.assertNotNull(configuration);
        Assert.assertEquals("value1", configuration.evaluateToString("key1"));
        Assert.assertEquals("value2", configuration.evaluateToString("key2"));
        Assert.assertEquals("value3", configuration.evaluateToString("key3"));
        Assert.assertEquals("value4", configuration.evaluateToString("key4"));
        Assert.assertEquals("value5", configuration.evaluateToString("key5"));
        Assert.assertEquals("value6", configuration.evaluateToString("key6"));
        Assert.assertEquals("value7", configuration.evaluateToString("key7"));
        Assert.assertEquals("value8", configuration.evaluateToString("key8"));

    }

}
