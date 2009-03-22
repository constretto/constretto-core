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
package org.constretto.model;

import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;
import org.constretto.ConfigurationStore;

import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class TaggedPropertySet {
    private final Class<? extends ConfigurationStore> storeClass;
    private final String tag;
    private final Map<String, String> properties;

    public TaggedPropertySet(Map<String, String> properties, Class<? extends ConfigurationStore> storeClass) {
        this.tag = ConfigurationNode.DEFAULT_TAG;
        this.properties = properties;
        this.storeClass = storeClass;
    }

    public TaggedPropertySet(String tag, Map<String, String> properties, Class<? extends ConfigurationStore> storeClass) {
        if (tag == null) {
            throw new IllegalArgumentException("A tagged property set cannot have \"null\" as tag");
        }
        this.tag = tag;
        this.properties = properties;
        this.storeClass = storeClass;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getTag() {
        return tag;
    }

    public Class<? extends ConfigurationStore> getStoreClass() {
        return storeClass;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
