package org.constretto.internal.store;

import com.google.gson.Gson;
import org.constretto.ConfigurationStore;
import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.*;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.Collections.addAll;

public class YamlStore implements ConfigurationStore {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Map<String, TaggedResource> resources;

    public YamlStore() {
        resources = new HashMap<String, TaggedResource>();
    }

    private YamlStore(YamlStore old, String key, TaggedResource resource) {
        resources = new HashMap<String, TaggedResource>();
        resources.putAll(old.resources);
        resources.put(key, resource);
    }

    public YamlStore addResource(Resource resource, String key, String... tags) {
        List<String> tagList = new ArrayList<String>();
        addAll(tagList, tags);
        return new YamlStore(this, key, new TaggedResource(resource, tagList));
    }

    public Collection<TaggedPropertySet> parseConfiguration() {
        List<TaggedPropertySet> properties = new ArrayList<TaggedPropertySet>();
        for (Map.Entry<String, TaggedResource> entry : resources.entrySet()) {
            TaggedResource taggedResource = entry.getValue();
            if (taggedResource.resource.exists()) {
                if (taggedResource.tags.isEmpty()) {
                    HashMap<String, String> property = new HashMap<String, String>();
                    property.put(entry.getKey(), yamlToJson(taggedResource.resource));
                    properties.add(new TaggedPropertySet(property, YamlStore.class));
                } else {
                    for (String tag : taggedResource.tags) {
                        HashMap<String, String> property = new HashMap<String, String>();
                        property.put(entry.getKey(), yamlToJson(taggedResource.resource));
                        properties.add(new TaggedPropertySet(tag, property, YamlStore.class));
                    }
                }
            }
        }
        return properties;
    }

    private String yamlToJson(Resource resource) {
        Yaml yaml = new Yaml();
        Map data = yaml.loadAs(new InputStreamReader(resource.getInputStream(), DEFAULT_CHARSET), Map.class);
        return new Gson().toJson(data);
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
