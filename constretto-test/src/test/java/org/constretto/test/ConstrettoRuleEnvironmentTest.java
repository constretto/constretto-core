package org.constretto.test;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * This source code is the property of NextGenTel AS
 *
 * @author sek
 */
@Environment(ConstrettoRuleEnvironmentTest.ENVIRONMENT_VALUE)
@RunWith(ConstrettoSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConstrettoRuleEnvironmentTest.TestConfiguration.class)
public class ConstrettoRuleEnvironmentTest {

    public static final String ENVIRONMENT_VALUE = "junit";

    @Autowired
    private TestBean testBean;

    @Configuration
    public static class TestConfiguration {

        @Autowired
        private BeanFactory beanFactory;

        @Bean
        public ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder(true).getConfiguration();
        }

        @Bean
        public ConfigurationAnnotationConfigurer configurationAnnotationConfigurer(final ConstrettoConfiguration configuration) {
            final ConfigurationAnnotationConfigurer configurer = new ConfigurationAnnotationConfigurer(configuration,
                                                                                                       new DefaultAssemblyContextResolver());
            configurer.setBeanFactory(beanFactory);
            return configurer;
        }

        @Bean
        TestBean testBean() {
            return new TestBean();
        }
    }



    @Test
    public void testApplyEnvironment() throws Exception {

        assertArrayEquals(new String[]{ENVIRONMENT_VALUE}, testBean.injectedEnvironment.toArray(new String[1]));

    }

    static final class TestBean {

        @Environment
        private List<String> injectedEnvironment;

        @ClassRule
        public static ConstrettoRule constrettoRule = new ConstrettoRule();

    }
}
