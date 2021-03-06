package org.constretto.internal.store.ldap;

import com.google.gson.Gson;
import org.constretto.ConfigurationStore;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.ConfigurationValue;
import org.constretto.model.TaggedPropertySet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.*;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class LdapConfigurationStore implements ConfigurationStore {

    private Map<String, Attributes> keyAttributesMap = Collections.emptyMap();
    private List<String> tags = Collections.emptyList();
    private final Gson gsonSerializer = new Gson();

    public LdapConfigurationStore() {
        tags = Arrays.asList(ConfigurationValue.DEFAULT_TAG);
    }

    private LdapConfigurationStore(List<String> tags) {
        this.tags = tags;
    }

    public LdapConfigurationStore(LdapConfigurationStore oldStore,
                                  String key,
                                  Attributes attributes, String... tags) {
        this(tags == null || tags.length == 0 ? Arrays.asList(ConfigurationValue.DEFAULT_TAG) : Arrays.asList(tags));
        this.keyAttributesMap = new HashMap<String, Attributes>(oldStore.keyAttributesMap.size() + 1);
        this.keyAttributesMap.putAll(oldStore.keyAttributesMap);
        this.keyAttributesMap.put(key, attributes);
    }

    public LdapConfigurationStore(LdapConfigurationStore oldStore, Attributes attributes, String... tags) {
        this(oldStore, null, attributes, tags);
    }

    public Collection<TaggedPropertySet> parseConfiguration() {

        Map<String, String> properties = new HashMap<String, String>(keyAttributesMap.size());

        for (String key : keyAttributesMap.keySet()) {
            properties.putAll(convertAttributesToProperties(key, keyAttributesMap.get(key)));
        }

        return createTaggedPropertySetsFromProperties(properties);
    }

    private List<TaggedPropertySet> createTaggedPropertySetsFromProperties(Map<String, String> properties) {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<TaggedPropertySet>(tags.size());
        for (String tag : tags) {
            taggedPropertySets.add(new TaggedPropertySet(tag, properties, getClass()));
        }
        return taggedPropertySets;
    }

    private Map<String, String> convertAttributesToProperties(String key, Attributes attributes) {

        Map<String, String> properties = new HashMap<String, String>(keyAttributesMap.size());
        try {
            final NamingEnumeration<? extends Attribute> attributesAll = attributes.getAll();
            while (attributesAll.hasMore()) {
                final Attribute attribute = attributesAll.next();
                if (!attribute.getID().contains("password")) {
                    properties.put(mergeKeyAndId(key, attribute.getID()),
                            convertAttribute(attribute));
                }

            }

        } catch (NamingException e) {
            throw new ConstrettoException("Could not read attributes from LDAP");
        }
        return properties;
    }

    private String convertAttribute(Attribute attribute) throws NamingException {
        final int subElementsCount = attribute.size();
        if(subElementsCount == 1) {
            return attribute.get().toString();
        } else {
            List<String> multiValue = new ArrayList<String>(subElementsCount);
            for(int i=0; i < subElementsCount; i++) {
                multiValue.add(attribute.get(i).toString());
            }
            return gsonSerializer.toJson(multiValue);
        }
    }

    private String mergeKeyAndId(String key, String id) {
        return key == null ? id : key + "." + id;
    }

}
