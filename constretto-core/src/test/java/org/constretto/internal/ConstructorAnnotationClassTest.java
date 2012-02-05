package org.constretto.internal;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configure;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.Resource;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *  @author <a href=mailto:zapodot.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstructorAnnotationClassTest {

    ConstrettoConfiguration config;

    @Before
    public void before() {
        config = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(new Resource("classpath:subClassData.properties"))
                .done()
                .getConfiguration();
    }


    @Test
    public void testIncjectOnConstructor() throws Exception {
        NonLocalConfigurationClass nonLocalConfigurationClassObject = config.as(NonLocalConfigurationClass.class);
        assertEquals("value", nonLocalConfigurationClassObject.getValue());

    }


    /**
     * Because we can not inject parameter values to @Configure annotated constructors in inner classes,
     * we expect a ConstrettoException
     *
     * @throws Exception
     */
    @Test(expected = ConstrettoException.class)
    public void testInjectOnConstructorInnerClass() throws Exception {
        config.as(InnerClassWithNoDefaultConstructor.class);
    }

    private class InnerClassWithNoDefaultConstructor {

        private String property;

        @Configure
        public InnerClassWithNoDefaultConstructor(final String value) {
            this.property = value;
        }

        public String getProperty() {
            return property;
        }
    }


}
