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
package org.constretto.spring.namespacehandler;

import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ImportWithDefaultResolverTest {
    private static ApplicationContext ctx;

    @BeforeClass
    public static void setEnvironment() {
        System.setProperty(DefaultAssemblyContextResolver.ASSEMBLY_KEY, "junit");
        ctx = new ClassPathXmlApplicationContext("org/constretto/spring/namespacehandler/ImportWithDefaultResolverTest-context.xml");
    }

    @Test
    public void givenJunitEnvironmentTheCorrectImportsAreProcessed() {
        Assert.assertTrue(ctx.containsBean("inMainConfig"));
        Assert.assertTrue(ctx.containsBean("inJunitConfig"));
        Assert.assertFalse(ctx.containsBean("inDevelopmentConfig"));
    }

    @BeforeClass
    public static void clearEnvironment() {
        System.clearProperty(DefaultAssemblyContextResolver.ASSEMBLY_KEY);
        ctx = null;
    }
}
