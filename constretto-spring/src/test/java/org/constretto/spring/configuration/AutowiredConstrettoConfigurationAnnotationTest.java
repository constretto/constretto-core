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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import org.constretto.spring.configuration.helper.*;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AutowiredConstrettoConfigurationAnnotationTest {
    @Resource
    private ValidBeanThatAllowsNull validBeanThatAllowsNull;
    @Resource
    private ValidBeanWithComplexDefault validBeanWithComplexDefault;
    @Resource
    private ValidBeanWithNoDefault validBeanWithNoDefault;
    @Resource
    private ValidBeanWithSimpleDefault validBeanWithSimpleDefault;
    @Resource
    private ValidBeanUsingPropertyEditors validBeanUsingPropertyEditors;

    @Test
    public void allowNullOnNotRequiredFieldsWithNoDefaultValue() {
        assertNull(validBeanThatAllowsNull.getUrl());
    }

    @Test
    public void useDefaultValueFactoryWhenPresent() {
        assertEquals(Locale.ITALY, validBeanWithComplexDefault.getLocale());
    }

    @Test
    public void whenCorrectKeyPresentThenRetreiveValueFromConfigurationProvider() {
        assertEquals("http://arktekk.no", validBeanWithNoDefault.getUrl());
    }

    @Test
    public void useSimpleDefaultValueWhenPresent() {
        assertEquals("http://arktekk.no", validBeanWithSimpleDefault.getUrl());
    }

    @Test
    public void usePropertyEditorsWhenNeeded() {
        assertEquals(new Long(4), validBeanUsingPropertyEditors.getLongProperty());
        assertNotNull(validBeanUsingPropertyEditors.getFileProperty());
        assertNotNull(validBeanUsingPropertyEditors.getResourceProperty());
    }

}
