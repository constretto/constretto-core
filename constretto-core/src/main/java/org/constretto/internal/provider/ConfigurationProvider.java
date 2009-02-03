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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.constretto.ConfigurationContextResolver;
import org.constretto.ConfigurationStore;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.model.ConfigurationElement;
import org.constretto.model.PropertySet;
/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationProvider {
    private ConstrettoConfiguration configuration;
    private List<ConfigurationStore> configurationStores = new ArrayList<ConfigurationStore>();
    private List<String> labels = new ArrayList<String>();

    public ConfigurationProvider() {}

    private ConfigurationProvider(ConfigurationStore... configurationStores) {
        this.configurationStores = asList(configurationStores);
    }

    public ConfigurationProvider(ConfigurationContextResolver configurationContextResolver) {
        this.labels = configurationContextResolver.getLabels();
    }

    public ConfigurationProvider(List<String> labels, List<ConfigurationStore> configurationStores) {
        this(configurationStores.toArray(new ConfigurationStore[configurationStores.size()]));
        this.labels = labels;
    }

    public ConfigurationProvider(ConfigurationContextResolver resolver, List<ConfigurationStore> configurationStores) {
        this(configurationStores.toArray(new ConfigurationStore[configurationStores.size()]));
        this.labels.addAll(resolver.getLabels());
    }

    public ConfigurationProvider addLabel(String label) {
        labels.add(label);
        return this;
    }

    public ConfigurationProvider setConfigurationContextResolver(ConfigurationContextResolver configurationContextResolver) {
        this.labels.addAll(configurationContextResolver.getLabels());
        return this;
    }

    public void addConfigurationStore(ConfigurationStore configurationStore) {
        configurationStores.add(configurationStore);
    }

    public ConstrettoConfiguration getConfiguration() {
        if (null == configuration) {
            configuration = buildConfiguration();
        }
        return configuration;
    }

    private ConstrettoConfiguration buildConfiguration() {
        ConfigurationElement rootElement = new ConfigurationElement("constretto-configuration");
        Collection<PropertySet> propertySets = loadPropertySets();
        for (PropertySet propertySet : propertySets) {
            if (allowLabel(propertySet.getLabel())) {
                Map<String, String> properties = propertySet.getProperties();
                for (String expression : properties.keySet()) {
                    rootElement.update(expression, properties.get(expression));
                }
            }
        }

        return new DefaultConstrettoConfiguration(rootElement);
    }

    private boolean allowLabel(String currentLabel) {
        return this.labels.isEmpty() || currentLabel == null || this.labels.contains(currentLabel);

    }

    private Collection<PropertySet> loadPropertySets() {
        List<PropertySet> propertySets = new ArrayList<PropertySet>();
        for (ConfigurationStore configurationStore : configurationStores) {
            propertySets.addAll(configurationStore.load());
        }
        return propertySets;
    }

}
