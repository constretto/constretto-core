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
package org.constretto.internal.provider;

import org.constretto.ConfigurationContextResolver;
import org.constretto.ConfigurationStore;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.model.ConfigurationElement;
import org.constretto.model.ConfigurationSet;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationProvider {
    private ConstrettoConfiguration configuration;
    private List<ConfigurationStore> configurationStores = new ArrayList<ConfigurationStore>();
    private List<String> tags = new ArrayList<String>();

    public ConfigurationProvider() {}

    private ConfigurationProvider(ConfigurationStore... configurationStores) {
        this.configurationStores = asList(configurationStores);
    }

    public ConfigurationProvider(ConfigurationContextResolver configurationContextResolver) {
        this.tags = configurationContextResolver.getTags();
    }

    public ConfigurationProvider(List<String> tags, List<ConfigurationStore> configurationStores) {
        this(configurationStores.toArray(new ConfigurationStore[configurationStores.size()]));
        this.tags = tags;
    }

    public ConfigurationProvider(ConfigurationContextResolver resolver, List<ConfigurationStore> configurationStores) {
        this(configurationStores.toArray(new ConfigurationStore[configurationStores.size()]));
        this.tags.addAll(resolver.getTags());
    }

    public ConfigurationProvider addTag(String tag) {
        tags.add(tag);
        return this;
    }

    public ConfigurationProvider setConfigurationContextResolver(ConfigurationContextResolver configurationContextResolver) {
        this.tags.addAll(configurationContextResolver.getTags());
        return this;
    }

    public ConfigurationProvider addConfigurationStore(ConfigurationStore configurationStore) {
        configurationStores.add(configurationStore);
        return this;
    }

    public ConstrettoConfiguration getConfiguration() {
        if (null == configuration) {
            configuration = buildConfiguration();
        }
        return configuration;
    }

    private ConstrettoConfiguration buildConfiguration() {
        ConfigurationElement rootElement = new ConfigurationElement("constretto-configuration");
        Collection<ConfigurationSet> configurationSets = loadPropertySets();
        for (ConfigurationSet configurationSet : configurationSets) {
            if (allowTag(configurationSet.getTag())) {
                Map<String, String> properties = configurationSet.getProperties();
                for (String expression : properties.keySet()) {
                    rootElement.update(expression, properties.get(expression));
                }
            }
        }

        return new DefaultConstrettoConfiguration(rootElement);
    }

    private boolean allowTag(String tagToMatch) {
        return (this.tags.isEmpty() && tagToMatch == null) || this.tags.contains(tagToMatch);

    }

    private Collection<ConfigurationSet> loadPropertySets() {
        List<ConfigurationSet> configurationSets = new ArrayList<ConfigurationSet>();
        for (ConfigurationStore configurationStore : configurationStores) {
            configurationSets.addAll(configurationStore.load());
        }
        return configurationSets;
    }

}
