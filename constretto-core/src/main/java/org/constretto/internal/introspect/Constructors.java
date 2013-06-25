package org.constretto.internal.introspect;

import org.constretto.annotation.Configure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for working with constructors
 */
public class Constructors {

    public static <T, A extends Annotation> Constructor[] findConstructorsWithAnnotation(Class<T> clazz, Class<A> annotation) {
        Constructor<?>[] constructors = clazz.getConstructors();
        List<Constructor<?>> annotatedConstructors = new ArrayList<Constructor<?>>();
        for(Constructor<?> constructor: constructors) {
            if(constructor.isAnnotationPresent(annotation)) {
                annotatedConstructors.add(constructor);
            }
        }
        return annotatedConstructors.isEmpty() ? null : annotatedConstructors.toArray(new Constructor[]{});
    }

    public static <T> Constructor[] findConstructorsWithConfigureAnnotation(Class<T> clazz) {
        return findConstructorsWithAnnotation(clazz, Configure.class);
    }
}
