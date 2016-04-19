package org.constretto.test.extractors;

import org.junit.runner.Description;

/**
 * @author zapodot
 */
public interface TagExtractor {

    String[] findTagsForTest(final Description testDescription);
}
