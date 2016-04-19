package org.constretto.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zapodot
 */
public class PredefinedConfigurationContextResolver implements ConfigurationContextResolver {

    private List<String> tags;

    private PredefinedConfigurationContextResolver(final List<String> tags) {
        this.tags = cloneTagList(tags);
    }

    private static List<String> cloneTagList(final List<String> tags) {
        if (tags == null) {
            return Collections.emptyList();
        } else {
            List<String> newList = new ArrayList<>(tags.size());
            for (String tag : tags) {
                newList.add(tag);
            }
            return newList;
        }
    }

    @Override
    public List<String> getTags() {
        return cloneTagList(this.tags);
    }

    public static PredefinedConfigurationContextResolver empty() {
        return new PredefinedConfigurationContextResolver(Collections.<String>emptyList());
    }

    public static PredefinedConfigurationContextResolver usingTags(final String... tags) {
        if (tags == null) {
            return empty();
        } else {
            return usingTagsList(Arrays.asList(tags));
        }
    }

    public static PredefinedConfigurationContextResolver usingTagsList(final List<String> tags) {
        return new PredefinedConfigurationContextResolver(tags);
    }

}
