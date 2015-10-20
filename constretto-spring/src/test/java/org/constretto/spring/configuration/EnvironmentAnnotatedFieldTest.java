/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.constretto.spring.configuration;

import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.assembly.helper.AlwaysDevelopmentEnvironmentResolver;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class EnvironmentAnnotatedFieldTest {

    @Test
    public void givenClassWithEnvironmentAnnotatedPropertyThenInjectEnvironment() throws Exception {
        TestClazz testClazz = new TestClazz();
        ConfigurationAnnotationConfigurer annotationConfigurer = new ConfigurationAnnotationConfigurer(
                new DefaultConstrettoConfiguration(null), new AlwaysDevelopmentEnvironmentResolver());
        annotationConfigurer.setBeanFactory(new TestBeanFactory());
        annotationConfigurer.postProcessAfterInstantiation(testClazz, "testBean");
        Assert.assertTrue(testClazz.getEnvironments().contains("development"));
    }

    private class TestClazz {

        @Environment
        private List<String> environments;

        public List<String> getEnvironments() {
            return environments;
        }
    }

    private class TestBeanFactory implements BeanFactory {

        @Override
        public Object getBean(final String name) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public <T> T getBean(final Class<T> requiredType) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Object getBean(final String name, final Object... args) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean containsBean(final String name) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
            return true;
        }

        @Override
        public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isTypeMatch(final String name, final Class<?> targetType) throws NoSuchBeanDefinitionException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String[] getAliases(final String name) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
