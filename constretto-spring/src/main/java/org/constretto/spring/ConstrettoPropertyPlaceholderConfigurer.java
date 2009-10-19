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
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

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
public class ConstrettoPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private ConstrettoConfiguration configuration;
    private boolean ignoreUnresolvedPlaceHolders;


    public ConstrettoPropertyPlaceholderConfigurer(ConstrettoConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvedPlaceHolders = ignoreUnresolvablePlaceholders;
		super.setIgnoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders);
	}

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String value = null;
        if (ignoreUnresolvedPlaceHolders) {
            value = configuration.evaluateTo(placeholder, "");
        } else {
            value = configuration.evaluateTo(String.class, placeholder);
        }
        return null != value ? value : null;
    }

}