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

import org.constretto.ConfigurationStore;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.model.ConfigurationNode;
import org.constretto.model.TaggedPropertySet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationProvider {
    private ConstrettoConfiguration configuration;
    private List<ConfigurationStore> configurationStores = new ArrayList<ConfigurationStore>();
    private List<String> tags = new ArrayList<String>();

    public ConfigurationProvider() {
    }

    public ConfigurationProvider addTag(String tag) {
        tags.add(tag);
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
        ConfigurationNode rootNode = ConfigurationNode.createRootElement();
        Collection<TaggedPropertySet> taggedPropertySets = loadPropertySets();
        for (TaggedPropertySet taggedPropertySet : taggedPropertySets) {
            Map<String, String> properties = taggedPropertySet.getProperties();
            for (String expression : properties.keySet()) {
                rootNode.update(expression, properties.get(expression), taggedPropertySet.getTag());
            }
        }

        return new DefaultConstrettoConfiguration(rootNode, tags);
    }

    private Collection<TaggedPropertySet> loadPropertySets() {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<TaggedPropertySet>();
        for (ConfigurationStore configurationStore : configurationStores) {
            taggedPropertySets.addAll(configurationStore.parseConfiguration());
        }
        return taggedPropertySets;
    }
}
