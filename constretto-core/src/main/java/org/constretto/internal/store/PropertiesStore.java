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

import org.apache.commons.lang.StringUtils;
import org.constretto.ConfigurationStore;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.TaggedPropertySet;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This is a store for text files implementing key=value pairs. Also, it supports adding a convention of tgsa to
 * ordinary properties. For tags, we use a specific prefix which can be configured by the user, whose default value is "@".
 * <p/>
 * Please see {@link org.constretto.ConstrettoBuilder#addCurrentTag(String)} for more information on the tag concept.
 *
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class PropertiesStore implements ConfigurationStore {

    private static final String DEFAULT_TAG_PREFIX = "@";
    private static final String PROPERTY_CONTEXT_SEPARATOR = ".";

    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * Tag prefix specifies the contract of which is used in property files to flag that an entry is coupled to a tag,
     * i.e.:
     *
     * @production.datasource.username=produser means that the property datasource.username exists in context of the
     * production tag only.
     * <p/>
     * Unless anything else is specified, the default value is used, i.e. datasource.username=default value
     */
    private String tagPrefix;

    public PropertiesStore() {
        this.tagPrefix = DEFAULT_TAG_PREFIX;
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

    public List<TaggedPropertySet> parseConfiguration() {
        return getPropertySets();
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    private void addPropertiesToMap(Properties... props) {
        for (Properties p : props) {
            CollectionUtils.mergePropertiesIntoMap(p, this.properties);
        }
    }

    /**
     * Assumes that the passed resources wrap files that conform to {@link java.util.Properties}. The contents of these
     * files are added to the local representation of all application properties to be handled by this store.
     *
     * @param resources Spring resource paths to the property files used to back this store
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
     * Get all property sets that are relevant to this store, i.e. both tagged as well as untagged properties. A
     * single PropertySet is added per tag and then finally a single PropertySet containing all untaggef
     * properties.
     *
     * @return A list of all property sets, never null.
     */
    private List<TaggedPropertySet> getPropertySets() {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<TaggedPropertySet>();
        Set<String> tags = getTags(this.properties);
        for (String tag : tags) {
            taggedPropertySets.add(new TaggedPropertySet(tag, getPropertiesByTag(tag, this.properties)));
        }
        Map<String, String> unTaggedProperties = getUnTaggedProperties(this.properties);
        if (!unTaggedProperties.isEmpty()) {
            taggedPropertySets.add(new TaggedPropertySet(getUnTaggedProperties(this.properties)));
        }
        return taggedPropertySets;
    }

    private boolean isTag(String key) {
        return key.startsWith(tagPrefix);
    }

    private Map<String, String> getPropertiesByTag(String nonPrefixedTag, Map<String, String> allProperties) {
        String prefixedTag = prefixTag(nonPrefixedTag);

        Map<String, String> taggedProperties = new HashMap<String, String>();
        for (String key : allProperties.keySet()) {
            if (key.startsWith(prefixedTag)) {
                String strippedKey = stripTag(key, nonPrefixedTag);
                taggedProperties.put(strippedKey, allProperties.get(key));
            }
        }
        return taggedProperties;
    }

    /**
     * Get a map of untagged properties, i.e. their keys do not conform to {@link #isTag(String)}.
     *
     * @param properties a map of the properties of which to run through
     * @return a map of untagged properties, never null
     */
    private Map<String, String> getUnTaggedProperties(Map<String, String> properties) {
        Map<String, String> unTagged = new HashMap<String, String>();
        for (String key : properties.keySet()) {
            if (!isTag(key)) {
                unTagged.put(key, properties.get(key));
            }
        }
        return unTagged;
    }

    private Set<String> getTags(Map<String, String> properties) {
        Set<String> tags = new HashSet<String>();
        for (String key : properties.keySet()) {
            String tag = getTag(key);
            if (tag != null) {
                tags.add(tag);
            }
        }
        return tags;
    }

    /**
     * Remove the actual tag from the passed key. I.e. a key is flagged as a tag by the following
     * entry: @tag.key=value. This method removes the tag information, i.e. "@tag.".
     *
     * @param key full, tagged key
     * @return the trimmed key, i.e. untagged. For passed keys that are untagged, null is returned
     */
    private String getTag(String key) {
        if (isTag(key)) {
            return StringUtils.substringBetween(key, tagPrefix, PROPERTY_CONTEXT_SEPARATOR);
        } else {
            return null;
        }
    }

    private String prefixTag(String tag) {
        return tagPrefix + tag;
    }

    private String stripTag(String key, String tag) {
        return StringUtils.substringAfter(key, tagPrefix + tag + PROPERTY_CONTEXT_SEPARATOR);
    }
}
