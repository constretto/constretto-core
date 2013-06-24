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

/**
 *  @author Sondre Eikanger Kval&oslash;
 */
public class ConstructorAnnotationClassTest {

    ConstrettoConfiguration config;

    @Before
    public void before() {
        config = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(Resource.create("classpath:subClassData.properties"))
                .done()
                .getConfiguration();
    }


    @Test
    public void testInjectOnConstructor() throws Exception {
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

    @Test(expected = ConstrettoException.class)
    public void testInjectOnInterface() throws Exception {
        config.as(Serializable.class);
    }

    @Test(expected = ConstrettoException.class)
    public void testInjectOnAnonymousClass() throws Exception {
        Serializable serializableInstance = new Serializable() {};
        config.as(serializableInstance.getClass());
    }

    @Test(expected = ConstrettoException.class)
    public void testInjectOnClassWithMulitpleAnnotatedConstructors() throws Exception {
        config.as(NonLocalConfigurationClassMultipleConstructors.class);

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