package org.constretto.spring.javaconfig;

import org.constretto.ConstrettoBuilder;
import org.constretto.annotation.Configuration;
import org.constretto.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author zapodot at gmail dot com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BasicConstrettoConfigurationTest.TestContext.class)
public class BasicConstrettoConfigurationTest {

    public static final String DEFAULT_VALUE = "Default";

    @Autowired
    private TestBean testBean;

    @Autowired
    @Qualifier("requestScopedBean")
    private TestBean requestScopedTestBean;

    @Test
    public void testKeyConfigured() throws Exception {
        final String expectedValue = "value1";
        assertEquals(expectedValue, testBean.key1);
        assertEquals(expectedValue, testBean.key1AsValue);
        assertEquals(DEFAULT_VALUE, testBean.defaultValue);
        assertNull(requestScopedTestBean.key1);
        assertNull(requestScopedTestBean.key1AsValue);
        assertNull(requestScopedTestBean.defaultValue);
    }

    @org.springframework.context.annotation.Configuration
    public static class TestContext extends BasicConstrettoConfiguration {
        @Override
        public org.constretto.ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder()
                    .createPropertiesStore()
                    .addResource(Resource.create("classpath:properties/test1.properties"))
                    .done()
                    .getConfiguration();
        }

        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

        @Bean(name = "requestScopedBean")
        @Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
        public TestBean requestScopedBean() {
            return new TestBean();
        }

    }

    public static class TestBean {
        @Configuration(required = true)
        private String key1;

        @Value("${key1}")
        private String key1AsValue;

        @Value("${nothere:" + DEFAULT_VALUE + "}")
        private String defaultValue;
    }

}
