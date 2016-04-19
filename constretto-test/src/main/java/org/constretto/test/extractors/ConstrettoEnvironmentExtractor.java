package org.constretto.test.extractors;

import org.constretto.spring.annotation.Environment;
import org.junit.runner.Description;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;

/**
 * Class that helps extracting environment information from a test Class. Used by the {@link org.constretto.test.ConstrettoRule} class.
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConstrettoEnvironmentExtractor implements TagExtractor {

    @Override
    public String[] findTagsForTest(final Description testDescription) {
        Class<? extends Annotation> environmentAnnotationType = environmentAnnotation();
        return environmentAnnotationType == null ? null : testDescription.getTestClass().getAnnotation(environmentAnnotationType).
        final Environment environment = testDescription.getTestClass().getAnnotation();
        return environment == null ? null : environment.value();
    }

    private <T extends Annotation> T extractAnnotationValueForTestClass(final Class<?> testClazz) {
        Class<? extends Annotation> environmentAnnotationType = environmentAnnotation();
        if (environmentAnnotationType != null) {
            final Annotation annotation = testClazz.getAnnotation(environmentAnnotationType);
            new Reflections()
        } else {
            return null;
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
     * Extracts the value of the {@link Environment} annotation for the test class containing the given test method.
     *
     * @param description
     * @return an array with the specified values or null
     */
    public static String[] extractEnvironmentValue(final Description description) {
        return new ConstrettoEnvironmentExtractor().findTagsForTest(description);
    }

}
