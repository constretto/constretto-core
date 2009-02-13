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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.PropertySet;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

/**
 * This is a store for text files implementing key=value pairs. Also, it supports adding a convention of labels to
 * ordinary properties. For labels, we use a specific prefix which can be configured by the user, whose default value is "@".
 *
 * Please see {@link org.constretto.Constretto#addLabel(String)} for more information on the label concept.
 *
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class PropertiesStore extends AbstractConfigurationStore {

    private static final String DEFAULT_LABEL_PREFIX = "@";
    private static final String PROPERTY_CONTEXT_SEPARATOR = ".";

    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * Label prefix specifies the contract of which is used in property files to flag that an entry is coupled to a label,
     * i.e.:
     * @production.datasource.username=produser means that the property datasource.username exists in context of the
     * production label only.
     *
     * Unless anything else is specified, the default value is used, i.e. ´@´.
     *
     */
    private String labelPrefix;

    public PropertiesStore() {
        this.labelPrefix = DEFAULT_LABEL_PREFIX;
    }

    public PropertiesStore(Properties... properties) {
        this();
        addPropertiesToMap(properties);
    }

    public PropertiesStore addProperties(Properties props) {
        addPropertiesToMap(props);
        return this;
    }

    public PropertiesStore addResource(Resource resource) {
        addResourcesAsProperties(resource);
        return this;
    }

    public PropertiesStore(Resource... resources) {
        addResourcesAsProperties(resources);
    }

    public List<PropertySet> load() {
        return getPropertySets();
    }

    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
    }

    private void addPropertiesToMap(Properties... props) {
        for (Properties p : props) {
            CollectionUtils.mergePropertiesIntoMap(p, this.properties);
        }
    }

    /**
     * Assumes that the passed resources wrap files that conform to {@link java.util.Properties}. The contents of these
     * files are added to the local representation of all application properties to be handled by this store.
     * @param resources
     */
    private void addResourcesAsProperties(Resource... resources) {
        for (Resource r : resources) {
            try {
                InputStream is = r.getInputStream();
                Properties props = new Properties();
                props.load(is);
                addPropertiesToMap(props);
            } catch (IOException e) {
                throw new ConstrettoException(e);
            }
        }
    }

    /**
     * Get all property sets that are relevant to this store, i.e. both labelled as well as unlabelled properties. A
     * single PropertySet is added per label and then finally a single PropertySet containing all non-labelled
     * properties.
     * @return A list of all property sets, never null.
     */
    private List<PropertySet> getPropertySets() {
        List<PropertySet> propertySets = new ArrayList<PropertySet>();
        Set<String> labels = getLabels(this.properties);
        for (String label : labels) {
            propertySets.add(new PropertySet(label, getPropertiesByLabel(label, this.properties)));
        }
        Map<String, String> nonLabelledProperties = getNonLabelledProperties(this.properties);
        if (!nonLabelledProperties.isEmpty()) {
            propertySets.add(new PropertySet(getNonLabelledProperties(this.properties)));
        }
        return propertySets;
    }

    private boolean isLabel(String key) {
        return key.startsWith(labelPrefix);
    }

    private Map<String, String> getPropertiesByLabel(String nonPrefixedLabel, Map<String, String> allProperties) {
        String prefixedLabel = prefixLabel(nonPrefixedLabel);

        Map<String, String> labelledProperties = new HashMap<String, String>();
        for (String key : allProperties.keySet()) {
            if (key.startsWith(prefixedLabel)) {
                String strippedKey = stripLabel(key, nonPrefixedLabel);
                labelledProperties.put(strippedKey, allProperties.get(key));
            }
        }
        return labelledProperties;
    }

    /**
     * Get a map of non-labelled properties, i.e. their keys do not conform to {@link #isLabel(String)}.
     * @param properties a map of the properties of which to run through
     * @return a map of non-labelled properties, never null
     */
    private Map<String, String> getNonLabelledProperties(Map<String, String> properties) {
        Map<String, String> nonLabelledProperties = new HashMap<String, String>();
        for (String key : properties.keySet()) {
            if (!isLabel(key)) {
                nonLabelledProperties.put(key, properties.get(key));
            }
        }
        return nonLabelledProperties;
    }

    private Set<String> getLabels(Map<String, String> properties) {
        Set<String> labels = new HashSet<String>();
        for (String key : properties.keySet()) {
    	    String label = getLabel(key);
            if (label != null) {
                labels.add(label);
            }
        }
        return labels;
    }

    /**
     * Remove the actual label from the passed key. I.e. a key is flagged as a label by the following
     * entry: @label.key=value. This method removes the label information, i.e. "@label.".
     * @param key full, labelled key
     * @return the trimmed key, i.e. non-labelled. For passed keys that are non-labelled, null is returned
     */
    private String getLabel(String key) {
        if (isLabel(key)) {
            return StringUtils.substringBetween(key, labelPrefix, PROPERTY_CONTEXT_SEPARATOR);
        } else {
            return null;
        }
    }

    private String prefixLabel(String label) {
        return labelPrefix + label;
    }

    private String stripLabel(String key, String label) {
        return StringUtils.substringAfter(key, labelPrefix + label + PROPERTY_CONTEXT_SEPARATOR);
    }
}
