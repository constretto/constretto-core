package org.constretto.spring;


import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.Resource;
import org.constretto.spring.annotation.Constretto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author zapodot at gmail dot com
 */
public class ConstrettoJavaConfigTest {

    public static final String BEAN_KEY_LIST = "keyList";
    public static final String BEAN_KEY_SET = "key_set";
    public static final String KEY_VALUE = "value7";

    @Test(expected = ConstrettoException.class)
    public void testTestContextWithoutConstretto() throws Exception {
        new AnnotationConfigApplicationContext(TestContextWithoutConstretto.class);

    }

    @Test
    public void testContextProvidingConstrettoConfiguration() throws Exception {
        final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestContextWithConstretto.class);
        assertNotNull(applicationContext);
        final List<String> keyList = applicationContext.getBean(BEAN_KEY_LIST, List.class);
        assertEquals(1, keyList.size());
        assertEquals(KEY_VALUE, keyList.get(0));
        final Set<String> keySet = applicationContext.getBean(BEAN_KEY_SET, Set.class);
        assertEquals(KEY_VALUE, keySet.iterator().next());


    }

    @Test
    public void testContextWithNotBeanPostProcessorsEnabled() throws Exception {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestContextWithNoConstrettoBeanPostProcessors.class);
        final List<String> keys = applicationContext.getBean(BEAN_KEY_LIST, List.class);
        // The substitution key should not be substituted for a value
        assertEquals("${key7}", keys.get(0));

    }

    @Constretto
    @Configuration
    public static class TestContextWithoutConstretto {

    }

    @Constretto
    @Configuration
    public static class TestContextWithConstretto {

        @org.constretto.annotation.Configuration("key7")
        private String key;

        @Bean
        public static ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder(true)
                    .createIniFileConfigurationStore()
                    .addResource(Resource.create("classpath:properties/test1.ini"))
                    .done()
                    .getConfiguration();
        }

        @Bean(name = BEAN_KEY_LIST)
        public List<String> keyList(@Value("${key7}") final String key) {
            return Arrays.asList(key);
        }

        @Bean(name = BEAN_KEY_SET)
        public Set<String> keySet() {
            final Set<String> set = new HashSet<String>();
            set.add(key);
            return set;
        }
    }

    @Configuration
    @Constretto(enableAnnotationSupport = false, enablePropertyPlaceholder = false)
    public static class TestContextWithNoConstrettoBeanPostProcessors {

        public static ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder(false).getConfiguration();
        }

        @Bean(name = BEAN_KEY_LIST)
        public List<String> keyList(@Value("${key7}") final String key) {
            return Arrays.asList(key);
        }

    }
}
