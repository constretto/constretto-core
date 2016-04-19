package org.constretto.resolver;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author zapodot
 */
public class PredefinedConfigurationContextResolverTest {

    @Test
    public void testEmpty() throws Exception {
        final PredefinedConfigurationContextResolver contextResolver = PredefinedConfigurationContextResolver.empty();
        assertEquals(0, contextResolver.getTags().size());

    }

    @Test
    public void testUsingTags() throws Exception {
        final String tag1 = "tag1";
        final String tag2 = "tag2";
        final List<String> tags = PredefinedConfigurationContextResolver.usingTags(tag1, tag2).getTags();
        assertEquals(tag1, tags.get(0));
        assertEquals(tag2, tags.get(1));
    }

    @Test
    public void testUsingTagsList() throws Exception {
        final String tag1 = "tag1";
        final String tag2 = "tag2";
        final List<String> originalTagList = Arrays.asList(tag1, tag2);
        final List<String> tags = PredefinedConfigurationContextResolver.usingTagsList(originalTagList).getTags();
        assertNotSame(tags, originalTagList);
        assertEquals(2, tags.size());
        assertEquals(tag1, tags.get(0));
        assertEquals(tag2, tags.get(1));
    }
}