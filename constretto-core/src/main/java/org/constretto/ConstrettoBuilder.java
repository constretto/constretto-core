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
package org.constretto;

import org.constretto.internal.provider.ConfigurationProvider;
import org.constretto.internal.store.IniFileConfigurationStore;
import org.constretto.internal.store.ObjectConfigurationStore;
import org.constretto.internal.store.PropertiesStore;
import org.constretto.internal.store.SystemPropertiesStore;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * Provides a fluent Java api to build a constretto configuration object.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoBuilder {

    private final ConfigurationProvider configurationProvider = new ConfigurationProvider();
    private final ConstrettoBuilder builder;

    public ConstrettoBuilder() {
        this.builder = this;
    }

    public ConstrettoConfiguration getConfiguration() {
        return configurationProvider.getConfiguration();
    }

    public ConstrettoBuilder addCurrentTag(String tag) {
        configurationProvider.addTag(tag);
        return this;
    }

    public ConstrettoBuilder addConfigurationStore(ConfigurationStore configurationStore) {
        configurationProvider.addConfigurationStore(configurationStore);
        return this;
    }

    public PropertiesStoreBuilder createPropertiesStore() {
        return new PropertiesStoreBuilder();
    }

    public IniFileConfigurationStoreBuilder createIniFileConfigurationStore() {
        return new IniFileConfigurationStoreBuilder();
    }

    public ConstrettoBuilder createSystemPropertiesStore() {
        configurationProvider.addConfigurationStore(new SystemPropertiesStore());
        return this;
    }

    public ObjectConfigurationStoreBuilder createObjectConfigurationStore() {
        return new ObjectConfigurationStoreBuilder();
    }


    //
    // Store builders
    //
    private interface StoreBuilder {
        public ConstrettoBuilder done();
    }

    public class PropertiesStoreBuilder implements StoreBuilder {
        private final PropertiesStore store = new PropertiesStore();

        public PropertiesStoreBuilder addProperties(Properties properties) {
            store.addProperties(properties);
            return this;
        }

        public PropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationProvider.addConfigurationStore(store);
            return builder;
        }
    }

    public class IniFileConfigurationStoreBuilder implements StoreBuilder {
        private final IniFileConfigurationStore store = new IniFileConfigurationStore();

        public IniFileConfigurationStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationProvider.addConfigurationStore(store);
            return builder;
        }
    }

    public class ObjectConfigurationStoreBuilder implements StoreBuilder {
        private final ObjectConfigurationStore store = new ObjectConfigurationStore();

        public ObjectConfigurationStoreBuilder addObject(Object object) {
            store.addObject(object);
            return this;
        }

        public ConstrettoBuilder done() {
            return builder;
        }
    }

}
