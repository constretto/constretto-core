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
import org.constretto.exception.ConstrettoException;
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.internal.ConstrettoAutowireCandidateResolver;
import org.constretto.spring.resolver.AssemblyContextResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.*;

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
        int lowestDiscoveredPriority = Integer.MAX_VALUE;
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            try {
                Class beanClass = Class.forName(beanDefinition.getBeanClassName());
                Environment environmentAnnotation = findEnvironmentAnnotation(beanClass);
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
    private Environment findEnvironmentAnnotation(Class beanClass) {
        if (beanClass.isAnnotationPresent(Environment.class)) {
            return (Environment) beanClass.getAnnotation(Environment.class);
        } else {
            return findEnvironmentMetaAnnotation(new HashSet<Annotation>(), beanClass.getAnnotations());
        }
    }

    private Environment findEnvironmentMetaAnnotation(Set<Annotation> visited, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Environment) {
                return (Environment) annotation;
            } else {
                if (!visited.contains(annotation)) {
                    visited.add(annotation);
                    Environment environment = findEnvironmentMetaAnnotation(visited, annotation.annotationType().getAnnotations());
                    if (environment != null) {
                        return environment;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void removeNonAnnotatedBeansFromAutowireForType(Class lookupClass, ConfigurableListableBeanFactory configurableListableBeanFactory) throws ClassNotFoundException {
        List<String> beanNames = new ArrayList<String>();
        Class[] interfaces = lookupClass.getInterfaces();
        for (Class anInterface : interfaces) {
            beanNames.addAll(asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, anInterface)));
        }
        List<BeanDefinition> potentialMatches = new ArrayList<BeanDefinition>();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            Class beanClass = Class.forName(beanDefinition.getBeanClassName());
            beanDefinition.setAttribute(INCLUDE_IN_COLLECTIONS, beanClass.getInterfaces());
            Environment environmentAnnotation = findEnvironmentAnnotation(beanClass);
            if (environmentAnnotation == null) {
                beanDefinition.setAutowireCandidate(false);
            } else {
                potentialMatches.add(beanDefinition);
            }
        }
        if (potentialMatches.size() == 1) {
            potentialMatches.get(0).setAutowireCandidate(true);
        } else {
            List<BeanDefinition> highestPriorityBeans = new ArrayList<BeanDefinition>();
            for (BeanDefinition potentialMatch : potentialMatches) {
                if (potentialMatch.isAutowireCandidate()) {
                    potentialMatch.setAutowireCandidate(false);
                    highestPriorityBeans = prioritizeBeans(potentialMatch, highestPriorityBeans);
                }
            }
            if (highestPriorityBeans.size() == 1) {
                highestPriorityBeans.get(0).setAutowireCandidate(true);
            } else {
                List<String> equalPriorityBeans = new ArrayList<String>();
                for (BeanDefinition highestPriorityBean : highestPriorityBeans) {
                    equalPriorityBeans.add(highestPriorityBean.getBeanClassName());
                }
                throw new ConstrettoException(
                        "More than one bean with the class or interface registered with same tag. Could resolve priority. To fix this, remove one of the following beans "
                                + equalPriorityBeans.toString());
            }
        }
    }

    private List<BeanDefinition> prioritizeBeans(BeanDefinition potentialMatch, List<BeanDefinition> highestPriorityBeans) throws ClassNotFoundException {
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        int matchPriority = getAutowirePriority(Class.forName(potentialMatch.getBeanClassName()));
        if (highestPriorityBeans.isEmpty()) {
            result.add(potentialMatch);
        } else {
            for (BeanDefinition highestPriorityBean : highestPriorityBeans) {
                int mostSpesificPriority = getAutowirePriority(Class.forName(highestPriorityBean.getBeanClassName()));
                if ((matchPriority - mostSpesificPriority) < 0) {
                    result.clear();
                    result.add(potentialMatch);
                } else if (((matchPriority - mostSpesificPriority) == 0)) {
                    result.add(potentialMatch);
                    result.add(highestPriorityBean);
                } else {
                    result.add(highestPriorityBean);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private int getAutowirePriority(Class beanClass) {
        Environment environmentAnnotation = findEnvironmentAnnotation(beanClass);
        if (environmentAnnotation != null) {
            List<String> environments = asList(environmentAnnotation.value());
            List<String> assemblyContext = parseCSV(assemblyContextResolver.getAssemblyContext());
            for (int i = 0; i < assemblyContext.size(); i++) {
                if (environments.contains(assemblyContext.get(i))) {
                    return i;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private boolean decideIfAutowireCandiate(String beanName, final Environment environmentAnnotation) {
        List<String> targetEnvironments = new ArrayList<String>(){{addAll(asList(environmentAnnotation.value()));}};
        validateAnnotationValues(beanName, targetEnvironments);
        List<String> assemblyContext = parseCSV(assemblyContextResolver.getAssemblyContext());
        targetEnvironments.retainAll(assemblyContext);
        boolean autowireCandidate = !targetEnvironments.isEmpty();
        if (autowireCandidate) {
            logger.info(beanName + " is annotated with environment '" + environmentAnnotation.value()
                    + "', and is selected for autowiring in the current environment '"
                    + assemblyContextResolver.getAssemblyContext() + "'");
        } else {
            logger.info(beanName + " is annotated with environment '" + environmentAnnotation.value()
                    + "', and is discarded for autowiring in the current environment '"
                    + assemblyContextResolver.getAssemblyContext() + "'");
        }
        return autowireCandidate;
    }

    private void validateAnnotationValues(String beanName, List<String> beanEnvironments) {
        if (beanEnvironments.isEmpty()) {
            throw new ConstrettoException(
                    "You must specify environment tags in @Environment. offending bean: "
                            + beanName);
        }
    }

    private List<String> parseCSV(String csv) {
        List<String> elements = new ArrayList<String>();
        for (String element : csv.split(","))
            elements.add(element);
        return elements;
    }
}
