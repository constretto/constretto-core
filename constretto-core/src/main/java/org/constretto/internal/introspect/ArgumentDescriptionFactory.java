package org.constretto.internal.introspect;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Utility for resolving arguments
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ArgumentDescriptionFactory<A extends AccessibleObject> {

    private final Paranamer paranamer = new BytecodeReadingParanamer();
    private final A accessibleObject;
    private final Annotation[][] annotations;
    private final Class<?>[] parameterTypes;

    private ArgumentDescriptionFactory(A accessibleObject, Annotation[][] annotations, Class<?>[] parameterTypes) {
        this.accessibleObject = accessibleObject;
        this.annotations = annotations;
        this.parameterTypes = parameterTypes;

    }

    public ArgumentDescription[] resolveParameters() {
        final String[] names = paranamer.lookupParameterNames(accessibleObject);
        ArgumentDescription[] descriptions = new ArgumentDescription[names.length];
        for(int i = 0; i < names.length; i++) {
            descriptions[i] = new ArgumentDescription(names[i], annotations[i], parameterTypes[i]);
        }
        return descriptions;
    }

    public static ArgumentDescriptionFactory<Constructor> create(final Constructor<?> constructor) {
        return new ArgumentDescriptionFactory<Constructor>(constructor, constructor.getParameterAnnotations(), constructor.getParameterTypes());
    }

    public static ArgumentDescriptionFactory<Method> forMethod(final Method method) {
        return new ArgumentDescriptionFactory<Method>(method, method.getParameterAnnotations(), method.getParameterTypes());
    }
}
