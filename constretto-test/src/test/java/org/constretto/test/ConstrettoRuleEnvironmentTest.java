package org.constretto.test;

import java.util.List;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

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

    @Autowired
    @Qualifier("requestScopedBean")
    private TestBean requestScopedBean;

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

        @Bean(name = "requestScopedBean")
        @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
        TestBean requestScopedBean() {
            return new TestBean();
        }
    }



    @Test
    public void testApplyEnvironment() throws Exception {

        assertArrayEquals(new String[]{ENVIRONMENT_VALUE}, testBean.injectedEnvironment.toArray(new String[1]));

        assertNull(requestScopedBean.injectedEnvironment);
    }

    static class TestBean {

        @Environment
        private List<String> injectedEnvironment;

        @ClassRule
        public static ConstrettoRule constrettoRule = new ConstrettoRule();

    }
}
