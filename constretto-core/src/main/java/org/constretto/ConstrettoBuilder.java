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

import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.constretto.internal.store.*;
import org.constretto.model.ConfigurationValue;
import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;
import org.constretto.resolver.ConfigurationContextResolver;

import java.util.*;

/**
 * Provides a fluent Java api to build a constretto configuration object.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoBuilder {

    private final List<ConfigurationStore> configurationStores;
    private final List<String> tags;

    public ConstrettoBuilder() {
        this(new DefaultConfigurationContextResolver());
    }

    public ConstrettoBuilder(ConfigurationContextResolver configurationContextResolver) {
        this.configurationStores = new ArrayList<ConfigurationStore>();
        this.tags = new ArrayList<String>();
        for (String tag : configurationContextResolver.getTags()) {
            addCurrentTag(tag);
        }
    }

    private ConstrettoBuilder(List<ConfigurationStore> configurationStores, List<String> tags) {
        this.configurationStores = configurationStores;
        this.tags = tags;
    }

    public ConstrettoConfiguration getConfiguration() {
        Map<String, List<ConfigurationValue>> configuration = new HashMap<String, List<ConfigurationValue>>();
        Collection<TaggedPropertySet> taggedPropertySets = loadPropertySets();
        for (TaggedPropertySet taggedPropertySet : taggedPropertySets) {
            Map<String, String> properties = taggedPropertySet.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (configuration.containsKey(entry.getKey())) {
                    List<ConfigurationValue> values = configuration.get(entry.getKey());
                    if (values == null) {
                        values = new ArrayList<ConfigurationValue>();
                    }
                    values.add(new ConfigurationValue(entry.getValue(), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);

                } else {
                    List<ConfigurationValue> values = new ArrayList<ConfigurationValue>();
                    values.add(new ConfigurationValue(entry.getValue(), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);
                }
            }
        }

        return new DefaultConstrettoConfiguration(configuration, tags);
    }

    public ConstrettoBuilder addCurrentTag(String tag) {
        tags.add(tag);
        return new ConstrettoBuilder(configurationStores, tags);
    }

    public ConstrettoBuilder addConfigurationStore(ConfigurationStore configurationStore) {
        configurationStores.add(configurationStore);
        return new ConstrettoBuilder(configurationStores, tags);
    }

    public PropertiesStoreBuilder createPropertiesStore() {
        return new PropertiesStoreBuilder();
    }

    public EncryptedPropertiesStoreBuilder createEncryptedPropertiesStore(String passwordProperty) {
        return new EncryptedPropertiesStoreBuilder(passwordProperty);
    }

    public IniFileConfigurationStoreBuilder createIniFileConfigurationStore() {
        return new IniFileConfigurationStoreBuilder();
    }

    public ConstrettoBuilder createSystemPropertiesStore() {
        configurationStores.add(new SystemPropertiesStore());
        return new ConstrettoBuilder(configurationStores, tags);
    }

    public ObjectConfigurationStoreBuilder createObjectConfigurationStore() {
        return new ObjectConfigurationStoreBuilder();
    }

    private Collection<TaggedPropertySet> loadPropertySets() {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<TaggedPropertySet>();
        for (ConfigurationStore configurationStore : configurationStores) {
            taggedPropertySets.addAll(configurationStore.parseConfiguration());
        }
        return taggedPropertySets;
    }


    //
    // Store builders
    //

    private interface StoreBuilder {
        public ConstrettoBuilder done();
    }

    public class PropertiesStoreBuilder implements StoreBuilder {
        private final PropertiesStore store = new PropertiesStore();

        public PropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags);
        }
    }

    public class EncryptedPropertiesStoreBuilder implements StoreBuilder {
        private final EncryptedPropertiesStore store;

        public EncryptedPropertiesStoreBuilder(String passwordProperty) {
            store = new EncryptedPropertiesStore(passwordProperty);
        }

        public EncryptedPropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags);
        }
    }

    public class IniFileConfigurationStoreBuilder implements StoreBuilder {
        private final IniFileConfigurationStore store = new IniFileConfigurationStore();

        public IniFileConfigurationStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags);
        }
    }

    public class ObjectConfigurationStoreBuilder implements StoreBuilder {
        private final ObjectConfigurationStore store = new ObjectConfigurationStore();

        public ObjectConfigurationStoreBuilder addObject(Object object) {
            store.addObject(object);
            return this;
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags);
        }
    }

}
