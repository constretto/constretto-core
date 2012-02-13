package org.constretto.internal;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.model.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:torarnek@pvv.org">Tor Arne Kvaløy</a>
 */

public class FieldAnnotationSubClassTest {

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
    public void testSubClass() {
        SubClazz subClazz = new SubClazz();
        config.on(subClazz);

        assertEquals("value", subClazz.value);
        assertEquals("sub value", subClazz.subValue);
    }

    @Test
    public void testSubSubClass() {
        SubSubClazz subSubClazz = new SubSubClazz();
        config.on(subSubClazz);

        assertEquals("value", subSubClazz.value);
        assertEquals("sub value", subSubClazz.subValue);
        assertEquals("sub sub value", subSubClazz.subSubValue);
    }

    class Clazz {
        @Configuration
        public String value;
    }

    class SubClazz extends Clazz {
        @Configuration
        public String subValue;
    }

    class SubSubClazz extends SubClazz {
        @Configuration
        public String subSubValue;
    }
}
