package org.constretto.spring.configuration;

import org.constretto.spring.configuration.helper.AutowiredAndConfiguredConstructorInjectionBean;
import org.constretto.spring.configuration.helper.SimpleConstructorInjectableBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConstructorInjectionTest {

    @Autowired
    private SimpleConstructorInjectableBean simpleConstructorInjectableBean;

    @Autowired
    private AutowiredAndConfiguredConstructorInjectionBean autowiredAndConfiguredConstructorInjectionBean;

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(simpleConstructorInjectableBean);
        assertEquals("value1", simpleConstructorInjectableBean.getKey1());
    }

    @Test
    public void testConfiguredAndAutoWiredConstructor() throws Exception {
        assertNotNull(autowiredAndConfiguredConstructorInjectionBean);
        assertEquals("value2", autowiredAndConfiguredConstructorInjectionBean.getKey2());

    }
}
