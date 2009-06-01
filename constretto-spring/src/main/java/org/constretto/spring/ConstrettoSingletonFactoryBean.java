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

import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.constretto.spring.resolver.AssemblyContextResolver;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * A factory bean used to instantiate spring beans depending on the environment you are running in. This factory bean
 * use the {@link org.constretto.spring.resolver.AssemblyContextResolver} to lookup the running environment. the value of this property is used to
 * lookup in the map of beans.
 * <p/>
 * <h2>Usage :</h2>
 * <p/>
 * <pre>
 * &lt;bean id=&quot;productionBean&quot; class=&quot;com..ProductionTestBean&quot;&gt;
 *      &lt;property name=&quot;value&quot; value=&quot;production value&quot; /&gt;
 *  &lt;/bean&gt;
 * <p/>
 *  &lt;bean id=&quot;developmentBean&quot; class=&quot;com..DevelopmentTestBean&quot;&gt;
 *      &lt;property name=&quot;value&quot; value=&quot;development value&quot; /&gt;
 *  &lt;/bean&gt;
 * <p/>
 *  &lt;bean id=&quot;myBean&quot; class=&quot;org.constretto.spring.ConstrettoSingletonFactoryBean&quot;&gt;
 *      &lt;constructor-arg&gt;
 *          &lt;map&gt;
 *              &lt;entry key=&quot;production&quot; value-ref=&quot;productionBean&quot; /&gt;
 *              &lt;entry key=&quot;development&quot; value-ref=&quot;developmentBean&quot; /&gt;
 *          &lt;/map&gt;
 *      &lt;/constructor-arg&gt;
 *  &lt;/bean&gt;
 * <p/>
 *  &lt;bean id=&quot;myBeanOverriddenDefaultPrefix&quot; class=&quot;org.constretto.spring.propertyplaceholder.factory.ConstrettoSingletonFactoryBean&quot;&gt;
 *    &lt;constructor-arg&gt;
 *          &lt;map&gt;
 *              &lt;entry key=&quot;production&quot; value-ref=&quot;productionBean&quot; /&gt;
 *              &lt;entry key=&quot;development&quot; value-ref=&quot;developmentBean&quot; /&gt;
 *          &lt;/map&gt;
 *      &lt;/constructor-arg&gt;
 *      &lt;constructor-arg ref=&quot;developmentBean&quot; /&gt;
 *  &lt;/bean&gt;
 * <p/>
 * </pre>
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoSingletonFactoryBean implements FactoryBean {
    private Map<String, Object> beans;
    private Object defaultBean;
    private AssemblyContextResolver assemblyContextResolver;

    private ConstrettoSingletonFactoryBean() {
        assemblyContextResolver = new DefaultAssemblyContextResolver();
    }

    public ConstrettoSingletonFactoryBean(Map<String, Object> beans, Object defaultBean) {
        this();
        this.beans = beans;
        this.defaultBean = defaultBean;
    }

    public ConstrettoSingletonFactoryBean(Map<String, Object> beans) {
        this();
        this.beans = beans;
    }

    public ConstrettoSingletonFactoryBean(Map<String, Object> beans, AssemblyContextResolver assemblyContextResolver) {
        this.assemblyContextResolver = assemblyContextResolver;
        this.beans = beans;
    }

    public ConstrettoSingletonFactoryBean(Map<String, Object> beans, Object defaultBean,
                                          AssemblyContextResolver assemblyContextResolver) {
        this.assemblyContextResolver = assemblyContextResolver;
        this.beans = beans;
        this.defaultBean = defaultBean;
    }

    /**
     * Chooses the correct implementation to use given the current environment. if no environment is set. uses the class
     * with the default prefix (production if not set)
     */
    public Object getObject() throws Exception {
        if (null == beans && null == defaultBean) {
            throw new BeanInitializationException("At least one implementation of the service is mandatory");
        }
        return getResolvedBean();
    }

    @Autowired
    public void setAssemblyContextResolver(AssemblyContextResolver assemblyContextResolver) {
        this.assemblyContextResolver = assemblyContextResolver;
    }

    public void setDefaultBean(Object defaultBean) {
        this.defaultBean = defaultBean;
    }

    private Object getResolvedBean() {
        Object bean;
        String currentEnvironment = assemblyContextResolver.isAssemblyContextDefined() ? assemblyContextResolver
                .getAssemblyContext() : "[not defined]";
        if (assemblyContextResolver.isAssemblyContextDefined()) {
            bean = beans.get(currentEnvironment);
            if (null == bean) {
                bean = defaultBean;
            }
        } else {
            bean = defaultBean;
        }

        if (null == bean) {
            throw new BeanInitializationException("No bean assosiated with the prefix " + currentEnvironment
                    + ", and no default bean could be found");
        }
        return bean;
    }

    public Class<?> getObjectType() {
        if (null == beans) {
            throw new BeanInitializationException("At least one implementation of the service is mandatory");
        }

        return getResolvedBean().getClass();
    }

    public boolean isSingleton() {
        return false;
    }
}
