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

import org.constretto.ConfigurationStore;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.provider.ConfigurationProvider;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.constretto.internal.store.SystemPropertiesStore;
import org.constretto.resolver.ConfigurationContextResolver;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */

public class ConstrettoConfigurationFactoryBean implements FactoryBean {
    private final List<ConfigurationStore> stores;
    private ConfigurationContextResolver configurationContextResolver;
    private boolean systemPropertiesOverrides = true;

    public ConstrettoConfigurationFactoryBean(List<ConfigurationStore> stores) {
        this.stores = stores;
    }

    public Object getObject() throws Exception {
        ConfigurationContextResolver contextResolver = configurationContextResolver == null ? new DefaultConfigurationContextResolver() : configurationContextResolver;
        if (systemPropertiesOverrides) {
            stores.add(new SystemPropertiesStore());
        }
        ConfigurationProvider configurationProvider = new ConfigurationProvider(contextResolver, stores);
        return configurationProvider.getConfiguration();
    }

    public Class getObjectType() {
        return ConstrettoConfiguration.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setConfigurationContextResolver(ConfigurationContextResolver configurationContextResolver) {
        this.configurationContextResolver = configurationContextResolver;
    }

    public void setSystemPropertiesOverrides(boolean systemPropertiesOverrides) {
        this.systemPropertiesOverrides = systemPropertiesOverrides;
    }
}
