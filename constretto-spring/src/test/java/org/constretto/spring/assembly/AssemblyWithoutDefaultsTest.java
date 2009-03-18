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
package org.constretto.spring.assembly;

import static org.constretto.annotation.Environment.DEVELOPMENT;
import static org.constretto.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;
import static org.junit.Assert.assertEquals;

import org.constretto.spring.assembly.helper.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class AssemblyWithoutDefaultsTest {
    private ConfigurationService configurationService;

    @Before
    public void removeAssemblyKey() {
        System.clearProperty(ASSEMBLY_KEY);
    }

    @Test(expected = BeanCreationException.class)
    public void givenNoAssemblyContextWhenNoDefaultPresentThenFail() {
        loadContextAndInjectConfigurationService();
    }

    @Test(expected = BeanCreationException.class)
    public void givenUnRecognizedAssemblyContextWhenNoDefaultsPresentThenFail() {
        System.setProperty(ASSEMBLY_KEY, "noHit");
        loadContextAndInjectConfigurationService();
    }

    @Test
    public void givenRecognizedAssemblyContextAndNoDefaultsPresentThenChooseAnnotatedBeanWithGivenContext() {
        System.setProperty(ASSEMBLY_KEY, DEVELOPMENT);
        loadContextAndInjectConfigurationService();
        assertEquals(DEVELOPMENT, configurationService.getRunningEnvironment());
    }

    private void loadContextAndInjectConfigurationService() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "org/constretto/spring/assembly/AssemblyWithoutDefaultsTest-context.xml");
        configurationService = (ConfigurationService) ctx.getBean("configurationService");
    }
}
