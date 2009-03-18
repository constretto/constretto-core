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
package org.constretto.spring.propertyplaceholder;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.constretto.spring.propertyplaceholder.helper.TestBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConstrettoPropertyPlaceholderTest {
    @Resource
    TestBean testBean;

    @Test
    public void givenPlaceholderOnPropertyThenPlaceholderResolved() throws Exception {
        assertEquals("http://arktekk.no", testBean.getUrl());
    }

    @Test
    public void givenPlaceholderOnConstructorThenPlaceholderResolved() throws Exception {
        assertEquals("http://arktekk.no/coolService", testBean.getServiceUrl());
    }
}
