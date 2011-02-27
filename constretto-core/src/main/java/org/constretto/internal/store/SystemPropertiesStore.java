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
import org.constretto.model.ConfigurationValue;
import org.constretto.model.TaggedPropertySet;

import static java.lang.System.getProperties;
import static java.lang.System.getProperty;
import java.util.*;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class SystemPropertiesStore implements ConfigurationStore {

    public List<TaggedPropertySet> parseConfiguration() {
        final Map<String, String> properties = new HashMap<String, String>();
        Properties systemProperties = getProperties();
        Set<Map.Entry<String, String>> systemEnv = System.getenv().entrySet();
        for (Map.Entry<String, String> envEntry : systemEnv) {
            properties.put(envEntry.getKey(),envEntry.getValue());
        }
        for (Object key : systemProperties.keySet()) {
            String keyAsString = (String) key;
            properties.put(keyAsString, getProperty(keyAsString));
        }

        return new ArrayList<TaggedPropertySet>() {
            {
                add(new TaggedPropertySet(ConfigurationValue.ALL_TAG, properties, SystemPropertiesStore.class));
            }
        };
    }

}
