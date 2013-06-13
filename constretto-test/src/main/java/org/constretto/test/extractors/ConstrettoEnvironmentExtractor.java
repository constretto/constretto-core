package org.constretto.test.extractors;

import org.constretto.spring.annotation.Environment;
import org.junit.runner.Description;

/**
 * Class that helps extracting environment information from a test Class. Used by the {@link org.constretto.test.ConstrettoRule} class.
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoEnvironmentExtractor {

    /**
     * Extracts the value of the {@link Environment} annotation for the test class containing the given test method.
     *
     * @param description
     * @return an array with the specified values or null
     */
    public static String[] extractEnvironmentValue(final Description description) {
        final Environment environment = description.getTestClass().getAnnotation(Environment.class);
        return environment == null ? null : environment.value();
    }

}
