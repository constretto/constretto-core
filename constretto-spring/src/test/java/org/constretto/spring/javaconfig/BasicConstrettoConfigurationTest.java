package org.constretto.spring.javaconfig;

import org.constretto.ConstrettoBuilder;
import org.constretto.annotation.Configuration;
import org.constretto.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot at gmail dot com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BasicConstrettoConfigurationTest.TestContext.class)
public class BasicConstrettoConfigurationTest {

    public static final String DEFAULT_VALUE = "Default";
    @Configuration(required = true)
    private String key1;

    @Value("${key1}")
    private String key1AsValue;

    @Value("${nothere:" + DEFAULT_VALUE + "}")
    private String defaultValue;

    @Test
    public void testKeyConfigured() throws Exception {
        final String expectedValue = "value1";
        assertEquals(expectedValue, key1);
        assertEquals(expectedValue, key1AsValue);
        assertEquals(DEFAULT_VALUE, defaultValue);
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
    }


}
