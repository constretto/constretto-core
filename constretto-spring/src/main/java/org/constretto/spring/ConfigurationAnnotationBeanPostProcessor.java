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
import org.constretto.annotation.Environment;
import org.constretto.internal.converter.ValueConverterRegistry;
import org.constretto.internal.resolver.DefaultAssemblyContextResolver;
import org.constretto.resolver.AssemblyContextResolver;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
 * @see Environment
 */
public class ConfigurationAnnotationBeanPostProcessor implements BeanPostProcessor {
    private ConstrettoConfiguration configuration;
    private AssemblyContextResolver assemblyContextResolver;

    @Autowired(required = false)
    public ConfigurationAnnotationBeanPostProcessor(ConstrettoConfiguration configuration) {
        this.configuration = configuration;
        assemblyContextResolver = new DefaultAssemblyContextResolver();
    }


    @Autowired(required = false)
    public ConfigurationAnnotationBeanPostProcessor(ConstrettoConfiguration configuration,
                                                    AssemblyContextResolver assemblyContextResolver) {
        this.configuration = configuration;
        this.assemblyContextResolver = assemblyContextResolver;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        injectConfiguration(bean);
        autowireEnvironment(bean);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void autowireEnvironment(final Object bean) {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Environment annotation = field.getAnnotation(Environment.class);
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalAccessException("Autowiring of environment not allowed on static fields");
                    }

                    if (assemblyContextResolver.isAssemblyContextDefined()) {
                        updateProperty(bean, field, assemblyContextResolver.getAssemblyContext());
                    }
                }
            }
        });
    }

    private void injectConfiguration(final Object bean) {
        configuration.on(bean);
    }

    private void updateProperty(Object beanInstance, Field field, String newValue) throws IllegalArgumentException,
            IllegalAccessException {
        field.setAccessible(true);
        Object convertedValue = null;
        if (field.getType().isEnum()) {
            Object[] enumConstants = field.getType().getEnumConstants();
            for (Object enumConstant : enumConstants) {
                if (enumConstant.toString().equals(newValue)){
                    convertedValue = enumConstant;
                }
            }
        } else {
            convertedValue = ValueConverterRegistry.convert(field.getType(), newValue);
        }
        field.set(beanInstance, convertedValue);
    }



    
}
