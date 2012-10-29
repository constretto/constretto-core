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
import org.constretto.internal.ConstrettoUtils;
import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.constretto.internal.ConstrettoUtils.mergePropertiesIntoMap;

/**
 * This is a store for text files implementing key=value pairs. Also, it supports adding a convention of tgsa to
 * ordinary properties. For tags, we use a specific prefix which can be configured by the user, whose default value is "@".
 * <p/>
 * Please see {@link org.constretto.ConstrettoBuilder#addCurrentTag(String)} for more information on the tag concept.
 *
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class PropertiesStore implements ConfigurationStore {

    private static final String TAG_PREFIX = "@";
    private static final String PROPERTY_CONTEXT_SEPARATOR = ".";
    private final Map<String, String> properties;


    public PropertiesStore() {
        this.properties = new HashMap<String, String>();
    }

    private PropertiesStore(Map<String, String> properties) {
        this.properties = properties;
    }

    public PropertiesStore addResource(Resource resource) {
        addResourcesAsProperties(resource);
        return new PropertiesStore(properties);
    }

    public List<TaggedPropertySet> parseConfiguration() {
        return getPropertySets();
    }

    private void addPropertiesToMap(Properties... props) {
        for (Properties p : props) {
            mergePropertiesIntoMap(parseProperties(p), this.properties);
        }
    }

    /**
     * Used by sublclasses
     *
     * @param props the properties currently read
     * @return the argument
     */
    protected Properties parseProperties(Properties props) {
        return props;
    }

    /**
     * Assumes that the passed resources wrap files that conform to {@link java.util.Properties}. The contents of these
     * files are added to the local representation of all application properties to be handled by this store.
     *
     * @param resources Spring resource paths to the property files used to back this store
     */
    private void addResourcesAsProperties(Resource... resources) {
        for (Resource r : resources) {
		 InputStream is = null;
            try {
                if (r.exists()) {
                    is = r.getInputStream();
                    Properties props = new Properties();
                    props.load(is);
                    addPropertiesToMap(props);
                }
            } catch (IOException e) {
                throw new ConstrettoException(e);
            } finally {
                try {
                    is.close();
                } catch (Exception e){
                }
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
            taggedPropertySets.add(new TaggedPropertySet(tag, getPropertiesByTag(tag, this.properties), getClass()));
        }
        Map<String, String> unTaggedProperties = getUnTaggedProperties(this.properties);
        if (!unTaggedProperties.isEmpty()) {
            taggedPropertySets.add(new TaggedPropertySet(getUnTaggedProperties(this.properties), getClass()));
        }
        return taggedPropertySets;
    }

    private boolean isTag(String key) {
        return key.startsWith(TAG_PREFIX);
    }

    private Map<String, String> getPropertiesByTag(String nonPrefixedTag, Map<String, String> allProperties) {
        String prefixedTag = prefixTag(nonPrefixedTag);

        Map<String, String> taggedProperties = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : allProperties.entrySet()) {
            if (entry.getKey().startsWith(prefixedTag)) {
                String strippedKey = stripTag(entry.getKey(), nonPrefixedTag);
                if (!strippedKey.equals("")) {
                    taggedProperties.put(strippedKey, entry.getValue());
                }
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
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!isTag(entry.getKey())) {
                unTagged.put(entry.getKey(), entry.getValue());
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
            return ConstrettoUtils.substringBetween(key, TAG_PREFIX, PROPERTY_CONTEXT_SEPARATOR);
        } else {
            return null;
        }
    }

    private String prefixTag(String tag) {
        return TAG_PREFIX + tag;
    }

    private String stripTag(String key, String tag) {
        return ConstrettoUtils.substringAfter(key, TAG_PREFIX + tag + PROPERTY_CONTEXT_SEPARATOR);
    }
}
