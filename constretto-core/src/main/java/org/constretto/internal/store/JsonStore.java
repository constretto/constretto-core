package org.constretto.internal.store;

import org.constretto.ConfigurationStore;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;

import java.io.*;
import java.util.*;

import static java.util.Collections.addAll;

public class JsonStore implements ConfigurationStore {
    private Map<String, TaggedResouce> resources;

    public JsonStore() {
        resources = new HashMap<String, TaggedResouce>();
    }

    private JsonStore(JsonStore old, String key, TaggedResouce resource) {
        resources = new HashMap<String, TaggedResouce>();
        resources.putAll(old.resources);
        resources.put(key, resource);
    }

    public JsonStore addResource(Resource resource, String key, String... tags) {
        List<String> tagList = new ArrayList<String>();
        addAll(tagList, tags);
        return new JsonStore(this, key, new TaggedResouce(resource, tagList));
    }

    public Collection<TaggedPropertySet> parseConfiguration() {
        List<TaggedPropertySet> properties = new ArrayList<TaggedPropertySet>();
        for (Map.Entry<String, TaggedResouce> entry : resources.entrySet()) {
            TaggedResouce taggedResouce = entry.getValue();
            if (taggedResouce.resource.exists()) {
                if (taggedResouce.tags.isEmpty()) {
                    HashMap<String, String> property = new HashMap<String, String>();
                    property.put(entry.getKey(), readJson(taggedResouce.resource));
                    properties.add(new TaggedPropertySet(property, JsonStore.class));
                } else {
                    for (String tag : taggedResouce.tags) {
                        HashMap<String, String> property = new HashMap<String, String>();
                        property.put(entry.getKey(), readJson(taggedResouce.resource));
                        properties.add(new TaggedPropertySet(tag, property, JsonStore.class));
                    }
                }
            }
        }
        return properties;
    }

    private String readJson(Resource resource) {
        try {
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            InputStream inputStream = resource.getInputStream();
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            return out.toString();
        } catch (IOException e) {
            throw new ConstrettoException("Could not read json file", e);
        }

    }

    private class TaggedResouce {
        public final Resource resource;
        public final List<String> tags = new ArrayList<String>();

        private TaggedResouce(Resource resource, List<String> tags) {
            this.tags.addAll(tags);
            this.resource = resource;
        }
    }
}
