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

import java.util.Map;

/**
 * ConfigurationSet represents a tagged subset of configuration loaded
 * by a ConfigurationStore.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationSet {
    private String tag;
    private int priority;

    private final Map<String, String> properties;

    public ConfigurationSet(Map<String, String> properties) {
        this.properties = properties;
    }

    public ConfigurationSet(String tag, Map<String, String> properties) {
        this.tag = tag;
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getTag() {
        return tag;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
