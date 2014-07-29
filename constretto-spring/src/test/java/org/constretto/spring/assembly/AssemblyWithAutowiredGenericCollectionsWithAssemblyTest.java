package org.constretto.spring.assembly;

import org.constretto.spring.assembly.helper.service.genericcollections.ProductService;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.constretto.spring.annotation.Environment.DEVELOPMENT;
import static org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;
import static org.junit.Assert.assertEquals;

/**
 * @author zapodot at gmail dot com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:org/constretto/spring/assembly/AssemblyWithAutowiredGenericCollectionsTest-context.xml")
@DirtiesContext
public class AssemblyWithAutowiredGenericCollectionsWithAssemblyTest {

    @ClassRule
    public static ProvideSystemProperty systemProperty = new ProvideSystemProperty(ASSEMBLY_KEY, DEVELOPMENT)
            .and("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");

    @Autowired
    private ProductService productService;

    @Test
    public void testHandlersSet() throws Exception {
        assertEquals(1, productService.getProductHandlers().size());

    }
}
