package org.constretto.test;

import org.constretto.spring.annotation.Environment;
import org.junit.runners.model.FrameworkMethod;

/**
 * Class that helps extracting environment information from a test Class. Used by the {@link ConstrettoRule} class.
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoEnvironmentExtractor {

    /**
     * Extracts the value of the {@link Environment} annotation for the test class containing the given test method.
     *
     * @param frameworkMethod
     * @return an array with the specified values or null
     */
    public static String[] extractEnvironmentValue(final FrameworkMethod frameworkMethod) {
        final Environment enviroment = frameworkMethod.getMethod().getDeclaringClass().getAnnotation(Environment.class);
        return enviroment == null ? null : enviroment.value();
    }

}
