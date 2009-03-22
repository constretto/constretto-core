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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.constretto.annotation.Environment;
import org.constretto.exception.ConstrettoException;
import org.constretto.resolver.AssemblyContextResolver;
import org.constretto.spring.internal.ConstrettoAutowireCandidateResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * A BeanFactoryBeanFactoryPostProcessor implementation that will if registered as a bean in a spring context, enable
 * the constretto autowiring capabilities in the container.
 * <p/>
 * <p/>
 * May be used on any existing configurations and in combination with all the standard context implementations from the
 * Spring framework.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class EnvironmentAnnotationConfigurer implements BeanFactoryPostProcessor {
    private final Log logger = LogFactory.getLog(getClass());
    private final AssemblyContextResolver assemblyContextResolver;
    public static final String INCLUDE_IN_COLLECTIONS = "includeInCollections";

    public EnvironmentAnnotationConfigurer(AssemblyContextResolver assemblyContextResolver) {
        this.assemblyContextResolver = assemblyContextResolver;
    }

    @SuppressWarnings("unchecked")
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {

        if (!(configurableListableBeanFactory instanceof DefaultListableBeanFactory)) {
            throw new IllegalStateException(
                    "EnvironmentAnnotationConfigurer needs to operate on a DefaultListableBeanFactory");
        }
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        defaultListableBeanFactory.setAutowireCandidateResolver(new ConstrettoAutowireCandidateResolver());
        String[] beanNames = configurableListableBeanFactory.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            try {
                Class beanClass = Class.forName(beanDefinition.getBeanClassName());
                Environment environmentAnnotation = (Environment) beanClass.getAnnotation(Environment.class);
                if (environmentAnnotation != null) {
                    if (assemblyContextResolver.isAssemblyContextDefined()) {
                        boolean autowireCandidate = decideIfAutowireCandiate(beanName, environmentAnnotation);
                        beanDefinition.setAutowireCandidate(autowireCandidate);
                        if (autowireCandidate) {
                            removeNonAnnotatedBeansFromAutowireForType(beanClass, configurableListableBeanFactory);
                        }
                    } else {
                        beanDefinition.setAutowireCandidate(false);
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new ConstrettoException("Could not instanciateclass [" + beanDefinition.getBeanClassName() + "] for bean [" + beanName + "]", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void removeNonAnnotatedBeansFromAutowireForType(Class lookupClass, ConfigurableListableBeanFactory configurableListableBeanFactory) throws ClassNotFoundException {
        List<String> beanNames = new ArrayList<String>();
        Class[] interfaces = lookupClass.getInterfaces();
        for (Class anInterface : interfaces) {
            beanNames.addAll(Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, anInterface)));
        }

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            Class beanClass = Class.forName(beanDefinition.getBeanClassName());
            Environment environmentAnnotation = (Environment) beanClass.getAnnotation(Environment.class);
            if (environmentAnnotation == null) {
                beanDefinition.setAttribute(INCLUDE_IN_COLLECTIONS, beanClass.getInterfaces());
                beanDefinition.setAutowireCandidate(false);
            }
        }
    }

    private boolean decideIfAutowireCandiate(String beanName, Environment environmentAnnotation) {
        String environment = environmentAnnotation.value();
        String[] environmentList = environmentAnnotation.tags();
        validateAnnotationValues(beanName, environment, environmentList);
        List<String> targetEnvironments = new ArrayList<String>();
        targetEnvironments.add(environment);
        targetEnvironments.addAll(asList(environmentList));
        boolean autowireCandidate = targetEnvironments.contains(assemblyContextResolver.getAssemblyContext());
        if (autowireCandidate) {
            logger.info(beanName + " is annotated with environment '" + environment
                    + "', and is selected for autowiring in the current environment '"
                    + assemblyContextResolver.getAssemblyContext() + "'");
        } else {
            logger.info(beanName + " is annotated with environment '" + environment
                    + "', and is discarded for autowiring in the current environment '"
                    + assemblyContextResolver.getAssemblyContext() + "'");
        }
        return autowireCandidate;
    }

    private void validateAnnotationValues(String beanName, String beanEnvironment, String[] beanEnvironments) {
        if (!"".equals(beanEnvironment) && beanEnvironments.length > 0) {
            throw new ConstrettoException(
                    "You may not have both the value attribute and tags attribute specified on the @Environment annotation at the same time. offending bean: "
                            + beanName);
        }
        if ("".equals(beanEnvironment) && beanEnvironments.length == 0) {
            throw new ConstrettoException(
                    "You must specify eigther the value attribute or the tags attribute specified on the @Environment. offending bean: "
                            + beanName);
        }
    }
}
