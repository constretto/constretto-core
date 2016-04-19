package org.constretto.test.extractors;

import org.constretto.exception.ConstrettoException;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class that helps extracting environment information from a test Class. Used by the {@link org.constretto.test.ConstrettoRule} class.
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoEnvironmentExtractor implements TagExtractor {

    @Override
    public String[] findTagsForTest(final Description testDescription) {
        Class<? extends Annotation> environmentAnnotationType = environmentAnnotation();
        final Annotation envAnnotation = testDescription.getTestClass().getAnnotation(environmentAnnotationType);
        if (envAnnotation == null) {
            return null;
        } else {
            try {
                final Method valueMethod;
                valueMethod = envAnnotation.getClass().getMethod("value");
                return (String[]) valueMethod.invoke(envAnnotation);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new ConstrettoException("Could not extract environment from test class", e);
            }
        }

    }

    private Class<? extends Annotation> environmentAnnotation() {
        try {
            return (Class<Annotation>) Class.forName("org.constretto.spring.annotation.Environment");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    /**
     * Extracts the value of the {@link org.constretto.spring.annotation.Environment} annotation for the test class containing the given test method.
     *
     * @param description
     * @return an array with the specified values or null
     */
    public static String[] extractEnvironmentValue(final Description description) {
        return new ConstrettoEnvironmentExtractor().findTagsForTest(description);
    }

}
