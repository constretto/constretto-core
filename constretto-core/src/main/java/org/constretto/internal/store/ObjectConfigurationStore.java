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
import org.constretto.annotation.ConfigurationSource;
import org.constretto.model.ConfigurationSet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ObjectConfigurationStore extends AbstractConfigurationStore {
    private final List<Object> configurationObjects = new ArrayList<Object>();

    public ObjectConfigurationStore addObject(Object configurationObject) {
        configurationObjects.add(configurationObject);
        return this;
    }

    public Collection<ConfigurationSet> load() {
        Map<String, ConfigurationSet> propertySets = new HashMap<String, ConfigurationSet>();
        for (Object configurationObject : configurationObjects) {
            ConfigurationSet configurationSet = createPropertySetForObject(configurationObject);
            if (propertySets.containsKey(configurationSet.getTag())) {
                ConfigurationSet orginialSet = propertySets.get(configurationSet.getTag());
                orginialSet.getProperties().putAll(configurationSet.getProperties());
                propertySets.put(configurationSet.getTag(), orginialSet);
            } else {
                propertySets.put(configurationSet.getTag(), configurationSet);
            }
        }

        return propertySets.values();
    }

    private ConfigurationSet createPropertySetForObject(Object configurationObject) {
        String tag = null;
        String basePath = "";
        Map<String, String> properties = new HashMap<String, String>();
        if (configurationObject.getClass().isAnnotationPresent(ConfigurationSource.class)) {
            ConfigurationSource configurationAnnotation = configurationObject.getClass().getAnnotation(ConfigurationSource.class);
            tag = configurationAnnotation.tag();
            if (tag.equals("")) {
                tag = null;
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

        return new ConfigurationSet(tag, properties);
    }
}
