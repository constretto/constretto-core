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

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.internal.ScalaWrapperConstrettoConfiguration;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.constretto.internal.store.*;
import org.constretto.model.*;
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
    private final boolean enableSystemProps;
    private final Parser parser = new GsonParser();

    public ConstrettoBuilder() {
        this(new DefaultConfigurationContextResolver(), true);
    }

    public ConstrettoBuilder(boolean enableSystemProps) {
        this(new DefaultConfigurationContextResolver(), enableSystemProps);
    }

    public ConstrettoBuilder(ConfigurationContextResolver configurationContextResolver, boolean enableSystemProps) {
        this.configurationStores = new ArrayList<ConfigurationStore>();
        this.tags = new ArrayList<String>();
        this.enableSystemProps = enableSystemProps;
        for (String tag : configurationContextResolver.getTags()) {
            addCurrentTag(tag);
        }
        if (enableSystemProps) {
            configurationStores.add(new SystemPropertiesStore());
        }
    }

    private ConstrettoBuilder(List<ConfigurationStore> configurationStores, List<String> tags, boolean enableSystemProps) {
        this.enableSystemProps = enableSystemProps;
        this.configurationStores = configurationStores;
        this.tags = tags;
    }


    public ConstrettoConfiguration getConfiguration() {
        return getConfiguration(false);
    }

    public ConstrettoConfiguration getConfiguration(boolean scalaWrapper) {
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
                    values.add(new ConfigurationValue(parser.parse(entry.getValue()), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);

                } else {
                    List<ConfigurationValue> values = new ArrayList<ConfigurationValue>();
                    values.add(new ConfigurationValue(parser.parse(entry.getValue()), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);
                }
            }
        }
        if (scalaWrapper) {
            return new ScalaWrapperConstrettoConfiguration(configuration,tags);
        } else {
            return new DefaultConstrettoConfiguration(configuration, tags);
        }
    }

    public ConstrettoBuilder addCurrentTag(String tag) {
        tags.add(tag);
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
    }

    public ConstrettoBuilder addConfigurationStore(ConfigurationStore configurationStore) {
        configurationStores.add(configurationStore);
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
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
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
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
        private final PropertiesStore store;

        public PropertiesStoreBuilder() {
            this.store = new PropertiesStore();
        }

        private PropertiesStoreBuilder(PropertiesStore store) {
            this.store = store;
        }

        public PropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new PropertiesStoreBuilder(store);
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
        }
    }

    public class EncryptedPropertiesStoreBuilder implements StoreBuilder {
        private final EncryptedPropertiesStore store;

        public EncryptedPropertiesStoreBuilder(String passwordProperty) {
            store = new EncryptedPropertiesStore(passwordProperty);
        }

        private EncryptedPropertiesStoreBuilder(EncryptedPropertiesStore store) {
            this.store = store;
        }

        public EncryptedPropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new EncryptedPropertiesStoreBuilder(store);
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
        }
    }

    public class IniFileConfigurationStoreBuilder implements StoreBuilder {
        private final IniFileConfigurationStore store;

        public IniFileConfigurationStoreBuilder() {
            store = new IniFileConfigurationStore();
        }

        public IniFileConfigurationStoreBuilder(IniFileConfigurationStore store) {
            this.store = store;
        }

        public IniFileConfigurationStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new IniFileConfigurationStoreBuilder(store);
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
        }
    }

    public class ObjectConfigurationStoreBuilder implements StoreBuilder {
        private final ObjectConfigurationStore store;

        public ObjectConfigurationStoreBuilder() {
            store = new ObjectConfigurationStore();
        }

        public ObjectConfigurationStoreBuilder(ObjectConfigurationStore store) {
            this.store = store;
        }

        public ObjectConfigurationStoreBuilder addObject(Object object) {
            store.addObject(object);
            return new ObjectConfigurationStoreBuilder(store);
        }

        public ConstrettoBuilder done() {
            configurationStores.add(store);
            return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
        }
    }

}
