package org.constretto.test.extractors;

import org.constretto.annotation.Tags;
import org.junit.runner.Description;

/**
 * Utility for extracting values specified either on the method or class (or suite) level
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoTagExtractor implements TagExtractor {


    @Override
    public String[] findTagsForTest(final Description testDescription) {
        final Tags tagValues = getClassAnnotation(testDescription);
        if (tagValues == null) {
            return null;
        } else {
            return tagValues.value();
        }
    }

    public static String[] findTagValueForDescription(final Description description) {
        return new ConstrettoTagExtractor().findTagsForTest(description);
    }

    private static Tags getClassAnnotation(final Description description) {
        return description.getTestClass().getAnnotation(Tags.class);
    }

}
