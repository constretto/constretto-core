/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.spring;

import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.exception.ConstrettoException;
import org.constretto.internal.introspect.ArgumentDescription;
import org.constretto.internal.introspect.ArgumentDescriptionFactory;
import org.constretto.internal.introspect.Constructors;
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.resolver.AssemblyContextResolver;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link BeanPostProcessor} implementation that autowires annotated fields annotated with the &#064;Configuration or
 * &#064;Environment annotations.
 * <p/>
 * <p/>
 * Fields are injected right after construction of a bean, before any config methods are invoked. Such a config field
 * does not have to be public.
 * <p/>
 * <p/>
 * If a &#064;Configuration element have the required flag set, and no value could be assosiated with the given key a
 * {@link BeanInstantiationException} is thrown invalidating the entire context.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @see Configuration
 * @see org.constretto.spring.annotation.Environment
 */
public class ConfigurationAnnotationConfigurer extends InstantiationAwareBeanPostProcessorAdapter implements
        BeanFactoryPostProcessor {
    private ConstrettoConfiguration configuration;
    private AssemblyContextResolver assemblyContextResolver;
    private Map<Class<?>, Constructor<?>> configurableConstructorCache = Collections.synchronizedMap(new HashMap<Class<?>, Constructor<?>>());
    private final static Object constructorCacheLockObject = new Object();

    public ConfigurationAnnotationConfigurer(ConstrettoConfiguration configuration,
                                             AssemblyContextResolver assemblyContextResolver) {
        this.configuration = configuration;
        this.assemblyContextResolver = assemblyContextResolver;
    }

    @Override
    public Constructor<?>[] determineCandidateConstructors(final Class<?> beanClass, final String beanName) throws
            BeansException {

        final Constructor<?> constructor = configurableConstructorCache.get(beanClass);
        if (constructor == null) {
            return null;
        } else {
            return new Constructor<?>[]{constructor};
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        injectConfiguration(bean);
        injectEnvironment(bean);
        return true;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void injectEnvironment(final Object bean) {
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Environment.class)) {
                    field.setAccessible(true);
                    field.set(bean, assemblyContextResolver.getAssemblyContext());
                }
            }
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(bean.getClass(), "Could not inject Environment on spring bean");
        }
    }

    private void injectConfiguration(final Object bean) {
        configuration.on(bean);
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            final Class<?> beanType = beanFactory.getType(beanName);
            if (!configurableConstructorCache.containsKey(beanType)) {
                configurableConstructorCache.put(beanType, resolveConfigurableConstructor(beanType));
            }
            final Constructor<?> resolvedConstructor = configurableConstructorCache.get(beanType);
            if (resolvedConstructor != null) {
                final ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
                final ArgumentDescription[] argumentDescriptions = ArgumentDescriptionFactory.create(resolvedConstructor).resolveParameters();
                for (int i = 0; i < argumentDescriptions.length; i++) {
                    ArgumentDescription argumentDescription = argumentDescriptions[i];
                    if (!constructorArgumentValues.hasIndexedArgumentValue(i)) {
                        final String keyName = argumentDescription.constrettoConfigurationKeyCandidate();
                        if (configuration.hasValue(keyName)) {
                            constructorArgumentValues.addIndexedArgumentValue(i,
                                                                              configuration.evaluateTo(
                                                                                      argumentDescription.getType(),
                                                                                      keyName));
                        }
                    }
                }
            }

        }
    }

    private Constructor<?> resolveConfigurableConstructor(Class<?> beanClass) {
        final Constructor[] constructorsWithConfigureAnnotation = Constructors.findConstructorsWithConfigureAnnotation(
                beanClass);
        if (constructorsWithConfigureAnnotation == null) {
            return null;
        } else {
            if (constructorsWithConfigureAnnotation.length > 1) {
                throw new ConstrettoException("You should only have one constructor annotated with @Configure");
            }
            return constructorsWithConfigureAnnotation[0];
        }
    }
}
