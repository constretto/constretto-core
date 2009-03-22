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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.*;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * An adaption of the spring framework {@link PropertyPlaceholderConfigurer} class, using an implementation of the
 * {@link ConstrettoConfiguration} interface to resolve keys
 * <p/>
 * <h2>Example :</h2>
 * <p/>
 * <pre>
 * &lt;bean class=&quot;org.constretto.spring.ConstrettoPropertyPlaceholderConfigurer&quot;&gt;
 *   &lt;constructor-arg ref=&quot;someProvider&quot; /&gt;
 * &lt;/bean&gt;
 * <p/>
 * &lt;bean id=&quot;myBean&quot; class=&quot;com.example.MyClass&quot;&gt;
 *   &lt;property name=&quot;myProperty&quot; value=&quot;${propertyKey}&quot; /&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p/>
 * Note : The provider property of the placeholder configurer are marked as &#064;Autowired, and can be autowired in a
 * annotation config application context.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @see ConstrettoConfiguration
 * @see PropertyPlaceholderConfigurer
 */
public class ConstrettoPropertyPlaceholderConfigurer implements BeanFactoryPostProcessor, BeanNameAware,
        BeanFactoryAware {

    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private ConstrettoConfiguration configuration;

    private String beanName;
    private BeanFactory beanFactory;
    private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;
    private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

    public ConstrettoPropertyPlaceholderConfigurer(ConstrettoConfiguration configuration) {
        this.configuration = configuration;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactoryToProcess) throws BeansException {
        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver();
        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (String beanName1 : beanNames) {
            if (!(beanName1.equals(beanName) && beanFactoryToProcess.equals(beanFactory))) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanName1);
                try {
                    visitor.visitBeanDefinition(bd);
                } catch (BeanDefinitionStoreException ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName1, ex.getMessage());
                }
            }
        }
        beanFactoryToProcess.resolveAliases(valueResolver);
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    private String parseStringValue(String strVal) throws BeanDefinitionStoreException {
        StringBuffer buf = new StringBuffer(strVal);

        int startIndex = strVal.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                String propVal = resolvePlaceholder(placeholder);
                if (propVal == null) {
                    throw new BeanDefinitionStoreException("Could not resolve placeholder '" + placeholder + "'");
                }
                buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderSuffix.length();
                } else {
                    return index;
                }
            } else if (StringUtils.substringMatch(buf, index, this.placeholderPrefix)) {
                withinNestedPlaceholder++;
                index = index + this.placeholderPrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    private String resolvePlaceholder(String key) {
        String value = configuration.evaluateTo(key, "");
        return null != value ? value : null;
    }

    /**
     * BeanDefinitionVisitor that resolves placeholders in String values, delegating to the
     * <code>parseStringValue</code> method of the containing class.
     */
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
        public String resolveStringValue(String strVal) throws BeansException {
            return parseStringValue(strVal);
        }
    }
}