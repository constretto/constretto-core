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
package org.constretto.spring.factorybean;

import static org.constretto.annotation.Environment.DEVELOPMENT;
import static org.constretto.annotation.Environment.TEST;
import static org.constretto.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;
import static org.junit.Assert.assertEquals;

import org.constretto.spring.factorybean.helper.DevelopmentTestBean;
import org.constretto.spring.factorybean.helper.EnvironmentService;
import org.constretto.spring.factorybean.helper.ProductionTestBean;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConstrettoSingletonFactoryBeanTest extends AbstractJUnit4SpringContextTests {

    @Test(expected = BeanCreationException.class)
    public void givenNoStageOnStartupAndNoDefaultThenThenThrowBeanCreationException() throws Exception {
        System.clearProperty(ASSEMBLY_KEY);
        EnvironmentService testBean = (EnvironmentService) applicationContext.getBean("testBean");
    }

    @Test
    public void givenNoStageOnStartupWithDefaultThenDefaultBeanIsSelected() throws Exception {
        System.clearProperty(ASSEMBLY_KEY);
        EnvironmentService testBeanOverriddenDefaultPrefix = (EnvironmentService) applicationContext
                .getBean("testBeanOverriddenDefaultBean");
        assertEquals(DevelopmentTestBean.class, testBeanOverriddenDefaultPrefix.getClass());
    }

    @Test
    public void givenDevelopmentStageOnStartupThenDevelopmentBeanIsSelected() throws Exception {
        System.setProperty(ASSEMBLY_KEY, DEVELOPMENT);
        EnvironmentService testBean = (EnvironmentService) applicationContext.getBean("testBean");
        assertEquals(DevelopmentTestBean.class, testBean.getClass());
    }

    @Test(expected = BeanCreationException.class)
    public void givenUnknownStageOnStartupAndNoDefaultThenThrowBeanCreationException() throws Exception {
        System.setProperty(ASSEMBLY_KEY, TEST);
        EnvironmentService testBean = (EnvironmentService) applicationContext.getBean("testBean");
        assertEquals(DevelopmentTestBean.class, testBean.getClass());
    }

    @Test
    public void givenUnknownStageOnStartupWithDefaultThenDefaultIsSelected() throws Exception {
        System.setProperty(ASSEMBLY_KEY, TEST);
        EnvironmentService testBean = (EnvironmentService) applicationContext.getBean("testBeanOverriddenDefaultBean");
        assertEquals(DevelopmentTestBean.class, testBean.getClass());
    }

    @After
    public void cleanup() throws Exception {
        System.clearProperty(ASSEMBLY_KEY);
    }
}
