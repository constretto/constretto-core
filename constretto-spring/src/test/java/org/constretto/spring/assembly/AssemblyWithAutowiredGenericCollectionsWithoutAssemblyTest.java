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

import org.constretto.spring.assembly.helper.service.genericcollections.ProductService;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:org/constretto/spring/assembly/AssemblyWithAutowiredGenericCollectionsTest-context.xml")
@DirtiesContext
public class AssemblyWithAutowiredGenericCollectionsWithoutAssemblyTest {

    @Autowired
    private ProductService productService;

    @ClassRule
    public static ClearSystemProperties clearSystemProperties = new ClearSystemProperties(ASSEMBLY_KEY);


    @Test
    public void givenNoAssemblyContextGenericCollectionsThenCorrectlyWireContext() throws Exception {

        assertEquals(2, productService.getProductHandlers().size());
    }


}
