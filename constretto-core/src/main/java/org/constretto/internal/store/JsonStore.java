package org.constretto.internal.store;

import org.constretto.ConfigurationStore;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.Collections.addAll;

public class JsonStore implements ConfigurationStore {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Map<String, TaggedResource> resources;

    public JsonStore() {
        resources = new HashMap<String, TaggedResource>();
    }

    private JsonStore(JsonStore old, String key, TaggedResource resource) {
        resources = new HashMap<String, TaggedResource>();
        resources.putAll(old.resources);
        resources.put(key, resource);
    }

    public JsonStore addResource(Resource resource, String key, String... tags) {
        List<String> tagList = new ArrayList<String>();
        addAll(tagList, tags);
        return new JsonStore(this, key, new TaggedResource(resource, tagList));
    }

    public Collection<TaggedPropertySet> parseConfiguration() {
        List<TaggedPropertySet> properties = new ArrayList<TaggedPropertySet>();
        for (Map.Entry<String, TaggedResource> entry : resources.entrySet()) {
            TaggedResource taggedResource = entry.getValue();
            if (taggedResource.resource.exists()) {
                if (taggedResource.tags.isEmpty()) {
                    HashMap<String, String> property = new HashMap<String, String>();
                    property.put(entry.getKey(), readJson(taggedResource.resource));
                    properties.add(new TaggedPropertySet(property, JsonStore.class));
                } else {
                    for (String tag : taggedResource.tags) {
                        HashMap<String, String> property = new HashMap<String, String>();
                        property.put(entry.getKey(), readJson(taggedResource.resource));
                        properties.add(new TaggedPropertySet(tag, property, JsonStore.class));
                    }
                }
            }
        }
        return properties;
    }

    private String readJson(Resource resource) {
        try {
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            InputStream inputStream = resource.getInputStream();
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n, DEFAULT_CHARSET));
            }
            return out.toString();
        } catch (IOException e) {
            throw new ConstrettoException("Could not read json file", e);
        }

    }

    private static class TaggedResource {
        public final Resource resource;
        public final List<String> tags = new ArrayList<String>();

        private TaggedResource(Resource resource, List<String> tags) {
            this.tags.addAll(tags);
            this.resource = resource;
        }
    }
}
