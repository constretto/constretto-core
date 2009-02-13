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

import org.constretto.exception.ConstrettoException;
import org.constretto.model.PropertySet;
import org.ini4j.IniFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class IniFileConfigurationStore extends AbstractConfigurationStore {
    private static final String DEFAULT_LABEL = "default";
    private List<Resource> resources = new ArrayList<Resource>();

    public IniFileConfigurationStore() {}

    public IniFileConfigurationStore(List<Resource> resources) {
        this.resources = resources;
    }

    public IniFileConfigurationStore addResource(Resource resource) {
        this.resources.add(resource);
        return this;
    }

    public List<PropertySet> load() {
        List<PropertySet> propertySets = new ArrayList<PropertySet>();
        for (Resource r : resources) {
            Preferences prefs = load(r);
            List<String> labels = getChildren(prefs);
            for (String label : labels) {
                Preferences node = prefs.node(label);
                List<String> keysPerNode = getKeys(node);
                Map<String, String> properties = new HashMap<String, String>();

                for (String key : keysPerNode) {
                    String value = node.get(key, null);
                    properties.put(key, value);
                }
                if (label.equals(DEFAULT_LABEL)) {
                    // Ensure context-less label
                    label = null;
                }
                PropertySet propertySet = new PropertySet(label, properties);
                propertySets.add(propertySet);
            }
        }
        return propertySets;
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
        } catch (BackingStoreException e) {
            throw new ConstrettoException(e);
        } catch (IOException e) {
            throw new ConstrettoException(e);
        }
    }

}
