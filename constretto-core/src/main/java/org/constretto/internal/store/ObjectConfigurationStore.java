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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.constretto.ConfigurationStore;
import org.constretto.annotation.ConfigurationSource;
import org.constretto.model.ConfigurationNode;
import org.constretto.model.TaggedPropertySet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ObjectConfigurationStore implements ConfigurationStore {
    private final List<Object> configurationObjects = new ArrayList<Object>();

    public ObjectConfigurationStore addObject(Object configurationObject) {
        configurationObjects.add(configurationObject);
        return this;
    }

    public Collection<TaggedPropertySet> parseConfiguration() {
        Map<String, TaggedPropertySet> propertySets = new HashMap<String, TaggedPropertySet>();
        for (Object configurationObject : configurationObjects) {
            TaggedPropertySet taggedPropertySet = createPropertySetForObject(configurationObject);
            if (propertySets.containsKey(taggedPropertySet.getTag())) {
                TaggedPropertySet orginialSet = propertySets.get(taggedPropertySet.getTag());
                orginialSet.getProperties().putAll(taggedPropertySet.getProperties());
                propertySets.put(taggedPropertySet.getTag(), orginialSet);
            } else {
                propertySets.put(taggedPropertySet.getTag(), taggedPropertySet);
            }
        }

        return propertySets.values();
    }

    private TaggedPropertySet createPropertySetForObject(Object configurationObject) {
        String tag = ConfigurationNode.DEFAULT_TAG;
        String basePath = "";
        Map<String, String> properties = new HashMap<String, String>();
        if (configurationObject.getClass().isAnnotationPresent(ConfigurationSource.class)) {
            ConfigurationSource configurationAnnotation = configurationObject.getClass().getAnnotation(ConfigurationSource.class);
            tag = configurationAnnotation.tag();
            if (tag.equals("")) {
                tag = ConfigurationNode.DEFAULT_TAG;
            }
            basePath = configurationAnnotation.basePath();
        }

        for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(configurationObject)) {
            boolean canRead = propertyDescriptor.getReadMethod() != null;
            boolean isString = propertyDescriptor.getPropertyType().isAssignableFrom(String.class);
            if (canRead && isString) {
                String path = propertyDescriptor.getName();
                try {
                    String value = (String) PropertyUtils.getProperty(configurationObject, path);
                    if (!StringUtils.isEmpty(basePath)) {
                        path = basePath + "." + path;
                    }
                    properties.put(path, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

        }

        return new TaggedPropertySet(tag, properties);
    }
}
