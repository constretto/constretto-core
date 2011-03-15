package org.constretto.internal;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * @author <a href="mailto:torarnek@pvv.org">Tor Arne Kvaløy</a>
 */

public class FieldAnnotationSubClassTest {

    ConstrettoConfiguration config;

    @Before
    public void before() {
        config = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(new DefaultResourceLoader().getResource("classpath:subClassData.properties"))
                .done()
                .getConfiguration();
    }

    @Test
    public void testSubClass() {
        SubClazz subClazz = new SubClazz();
        config.on(subClazz);

        Assert.assertEquals("value", subClazz.value);
        Assert.assertEquals("sub value", subClazz.subValue);
    }

    @Test
    public void testSubSubClass() {
        SubSubClazz subSubClazz = new SubSubClazz();
        config.on(subSubClazz);

        Assert.assertEquals("value", subSubClazz.value);
        Assert.assertEquals("sub value", subSubClazz.subValue);
        Assert.assertEquals("sub sub value", subSubClazz.subSubValue);
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
