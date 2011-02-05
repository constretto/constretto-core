package org.constretto.internal;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * @author <a href="mailto:torarnek@pvv.org">Tor Arne Kvaløy</a>
 */
public class MethodAnnotationSubClassTest {

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


    @Test
    public void checkThatDefaultMethodIsNotSet() {
        SubSubClazz subSubClazz = new SubSubClazz();
        config.on(subSubClazz);

        Assert.assertNull(subSubClazz.defaultMethodValue);
    }

    @Test
    public void checkThatPrivateMethodIsNotSet() throws Exception {
        SubSubClazz subSubClazz = new SubSubClazz();
        config.on(subSubClazz);

        Assert.assertNull(subSubClazz.privateMethodValue);
    }

    class Clazz {
        public String value;
        public String defaultMethodValue;
        public String privateMethodValue;

        @Configure
        public void setValue(String value) {
            this.value = value;
        }

        @Configure
        void setDefaultMethodValue(String defaultMethodValue) {
            this.defaultMethodValue = defaultMethodValue;
        }

        @Configure
        private void setPrivateMethodValue(String privateMethodValue) {
            this.privateMethodValue = privateMethodValue;
        }
    }

    class SubClazz extends Clazz {
        public String subValue;

        @Configure
        public void setSubValue(String subValue) {
            this.subValue = subValue;
        }
    }

    class SubSubClazz extends SubClazz {
        public String subSubValue;

        @Configure
        public void setSubSubValue(String subSubValue) {
            this.subSubValue = subSubValue;
        }
    }
}
