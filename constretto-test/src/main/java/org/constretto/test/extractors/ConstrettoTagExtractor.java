package org.constretto.test.extractors;

import org.constretto.annotation.Tags;
import org.junit.runner.Description;

/**
 * Utility for extracting values specified either on the method or class (or suite) level
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoTagExtractor {

    public static String[] findTagValueForDescription(final Description description) {
        final Tags tagValues = getClassAnnotation(description);
        if (tagValues == null) {
            return null;
        } else {
            return tagValues.value();
        }
    }

    private static Tags getClassAnnotation(final Description description) {
        return description.getTestClass().getAnnotation(Tags.class);
    }

}
