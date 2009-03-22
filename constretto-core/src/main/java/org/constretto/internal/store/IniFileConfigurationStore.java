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
package org.constretto.internal.store;

import org.constretto.ConfigurationStore;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.ConfigurationNode;
import org.constretto.model.TaggedPropertySet;
import org.ini4j.IniFile;
import org.springframework.core.io.Resource;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class IniFileConfigurationStore implements ConfigurationStore {
    private static final String DEFAULT_TAG = "default";
    private List<Resource> resources = new ArrayList<Resource>();

    public IniFileConfigurationStore() {
    }

    public IniFileConfigurationStore(List<Resource> resources) {
        this.resources = resources;
    }

    public IniFileConfigurationStore addResource(Resource resource) {
        this.resources.add(resource);
        return this;
    }

    public List<TaggedPropertySet> parseConfiguration() {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<TaggedPropertySet>();
        for (Resource r : resources) {
            if (r.exists()) {
                Preferences prefs = load(r);
                List<String> tags = getChildren(prefs);
                for (String tag : tags) {
                    Preferences node = prefs.node(tag);
                    List<String> keysPerNode = getKeys(node);
                    Map<String, String> properties = new HashMap<String, String>();

                    for (String key : keysPerNode) {
                        String value = node.get(key, null);
                        properties.put(key, value);
                    }
                    if (tag.equals(DEFAULT_TAG)) {
                        tag = ConfigurationNode.DEFAULT_TAG;
                    }
                    TaggedPropertySet taggedPropertySet = new TaggedPropertySet(tag, properties, getClass());
                    taggedPropertySets.add(taggedPropertySet);
                }
            }
        }
        return taggedPropertySets;
    }

    private List<String> getKeys(Preferences p) {
        try {
            return Arrays.asList(p.keys());
        } catch (BackingStoreException e) {
            throw new ConstrettoException(e);
        }
    }

    private List<String> getChildren(Preferences p) {
        try {
            return Arrays.asList(p.childrenNames());
        } catch (BackingStoreException e) {
            throw new ConstrettoException(e);
        }
    }

    private Preferences load(Resource resource) {
        try {
            return new IniFile(resource.getFile());
        } catch (Exception e) {
            throw new ConstrettoException(e);
        }
    }

    @Override
    public String toString() {
        return "Ini file store " + resources + ".";
    }
}
