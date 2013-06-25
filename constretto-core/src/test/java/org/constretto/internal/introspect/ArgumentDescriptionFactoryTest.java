package org.constretto.internal.introspect;

import org.constretto.annotation.Configure;
import org.constretto.internal.NonLocalConfigurationClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author zapodot
 */
public class ArgumentDescriptionFactoryTest {

    public static class ConfigurableClass {
        private String value;

        @Configure
        public void setValue(final String value) {
            this.value = value;
        }

    }
    @Test
    public void testForMethod() throws Exception {
        Method method = ConfigurableClass.class.getMethod("setValue", String.class);
        final ArgumentDescription[] argumentDescriptions = ArgumentDescriptionFactory.forMethod(method).resolveParameters();
        assertEquals(1, argumentDescriptions.length);
        assertEquals(String.class, argumentDescriptions[0].getType());
        assertEquals("value", argumentDescriptions[0].getName());
    }

    @Test
    public void testForConstrutor() throws Exception {
        final Constructor[] constructors = Constructors.findConstructorsWithConfigureAnnotation(
                NonLocalConfigurationClass.class);
        assertEquals(1, constructors.length);
        final ArgumentDescription[] argumentDescriptions = ArgumentDescriptionFactory.create(constructors[0]).resolveParameters();
        assertEquals("value", argumentDescriptions[0].getName());
        assertEquals(String.class, argumentDescriptions[0].getType());
    }

}
