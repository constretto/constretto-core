/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.internal;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.constretto.ConfigurationDefaultValueFactory;
import org.constretto.ConstrettoConfiguration;
import org.constretto.GenericConverter;
import org.constretto.Property;
import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;
import org.constretto.annotation.Tags;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoException;
import org.constretto.exception.ConstrettoExpressionException;
import org.constretto.internal.converter.ValueConverterRegistry;
import org.constretto.internal.introspect.Constructors;
import org.constretto.model.CPrimitive;
import org.constretto.model.CValue;
import org.constretto.model.ConfigurationValue;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Arrays.asList;
import static org.constretto.internal.GenericCollectionTypeResolver.*;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultConstrettoConfiguration implements ConstrettoConfiguration {
    private static final String NULL_STRING = "![![Null]!]!";

    private final Paranamer paranamer = new BytecodeReadingParanamer();

    protected final Map<String, List<ConfigurationValue>> configuration;
    private Set<WeakReference<Object>> configuredObjects = new CopyOnWriteArraySet<WeakReference<Object>>();
    private final List<String> originalTags = new ArrayList<String>();
    protected final List<String> currentTags = new ArrayList<String>();

    public DefaultConstrettoConfiguration(Map<String, List<ConfigurationValue>> configuration, List<String> originalTags) {
        this.configuration = configuration;
        this.originalTags.addAll(originalTags);
        this.currentTags.addAll(originalTags);
    }

    public DefaultConstrettoConfiguration(Map<String, List<ConfigurationValue>> configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public <K> K evaluateTo(String expression, K defaultValue) {
        if (!hasValue(expression)) {
            return defaultValue;
        }
        K value;
        try {
            value = (K) processAndConvert(defaultValue.getClass(), expression);
        } catch (ConstrettoConversionException e) {
            value = null;
        }
        return null != value ? value : defaultValue;
    }

    public <T> T evaluateWith(GenericConverter<T> converter, String expression) {
        ConfigurationValue value = findElementOrThrowException(expression);
        return converter.fromValue(value.value());
    }

    public CValue evaluate(String expression) throws ConstrettoExpressionException {
        return findElementOrThrowException(expression).value();
    }

    @SuppressWarnings("unchecked")
    public <K> List<K> evaluateToList(Class<K> targetClass, String expression) {
        ConfigurationValue value = findElementOrThrowException(expression);
        return (List<K>) ValueConverterRegistry.convert(targetClass, targetClass, value.value());
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> evaluateToMap(Class<K> keyClass, Class<V> valueClass, String expression) {
        ConfigurationValue value = findElementOrThrowException(expression);
        return (Map<K, V>) ValueConverterRegistry.convert(valueClass, keyClass, value.value());
    }

    public <K> K evaluateTo(Class<K> targetClass, String expression) throws ConstrettoExpressionException {
        return processAndConvert(targetClass, expression);
    }

    public String evaluateToString(String expression) throws ConstrettoExpressionException {
        return processAndConvert(String.class, expression);
    }

    public Boolean evaluateToBoolean(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Boolean.class, expression);
    }

    public Double evaluateToDouble(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Double.class, expression);
    }

    public Long evaluateToLong(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Long.class, expression);
    }

    public Float evaluateToFloat(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Float.class, expression);
    }

    public Integer evaluateToInt(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Integer.class, expression);
    }

    public Short evaluateToShort(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Short.class, expression);
    }

    public Byte evaluateToByte(String expression) throws ConstrettoExpressionException {
        return processAndConvert(Byte.class, expression);
    }

    public <T> T as(Class<T> configurationClass) throws ConstrettoException {
        T objectToConfigure;
        try {
            objectToConfigure = createInstance(configurationClass);
        } catch (ConstrettoException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConstrettoException("Could not instansiate class of type: " + configurationClass.getName()
                    + " when trying to inject it with configuration, It may be missing a default or @Configure annotated constructor", e);
        }
        injectConfiguration(objectToConfigure);
        return objectToConfigure;
    }

    public <T> T on(T objectToConfigure) throws ConstrettoException {
        injectConfiguration(objectToConfigure);
        return objectToConfigure;
    }

    public Map<String, String> asMap() {
        Map<String, String> properties = new HashMap<String, String>();
        for (Map.Entry<String, List<ConfigurationValue>> entry : configuration.entrySet()) {
            ConfigurationValue value = findElementOrNull(entry.getKey());
            if (value != null){
                properties.put(entry.getKey(), value.value().toString());
            }
        }
        return properties;
    }

    public boolean hasValue(String expression) {
        return findElementOrNull(expression) != null;
    }

    public void appendTag(String... newtags) {
        currentTags.addAll(asList(newtags));
        reconfigure();
    }

    public void prependTag(String... newtags) {
        currentTags.addAll(0, asList(newtags));
        reconfigure();
    }

    public void resetTags(boolean reconfigure) {
        currentTags.clear();
        currentTags.addAll(originalTags);
        if (reconfigure)
            reconfigure();
    }

    public void clearTags(boolean reconfigure) {
        currentTags.clear();
        originalTags.clear();
        if (reconfigure)
            reconfigure();
    }

    public void removeTag(String... newTags) {
        for (String newTag : newTags) {
            currentTags.remove(newTag);
        }
        reconfigure();
    }

    public List<String> getCurrentTags() {
        return currentTags;
    }

    public Iterator<Property> iterator() {
        List<Property> properties = new ArrayList<Property>();
        Map<String, String> map = asMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            properties.add(new Property(entry.getKey(), entry.getValue()));
        }
        return properties.iterator();
    }

    @Override
    public void reconfigure() {
        WeakReference[] references = configuredObjects.toArray(new WeakReference[configuredObjects.size()]);
        for (WeakReference reference : references) {
            if (reference != null && reference.get() != null) {
                on(reference.get());
            }
        }
    }

    //
    // Helper methods
    //
    private <T> T createInstance(final Class<T> configurationClass) throws InstantiationException, IllegalAccessException {


        if(configurationClass.isInterface()) {
            throw new ConstrettoException("Can not instantiate interfaces. You need to create an concrete implementing class first");
        }
        if (configurationClass.isAnonymousClass()) {
            throw new ConstrettoException("Can not instantiate anonymous classes using as(Class<T>. To inject configuration in to inner or anonymous classes, " +
                                                  "instantiate it first and call the on(T configuredObjecT) method");
        }
        Constructor<T>[] annotatedConstructors = findAnnotatedConstructorsOnClass(configurationClass);
        if(configurationClass.isMemberClass() && annotatedConstructors != null) {
            throw new ConstrettoException("Can not instantiate inner classes using a @Configure annotated constructor. " +
                                                  "To inject configuration, construct the instance yourself use the \"on(T configuredObject)\" method");
        }
        if(annotatedConstructors == null) {
            return configurationClass.newInstance();
        } else {
            if(annotatedConstructors.length > 1) {
                throw new ConstrettoException("More than one @Configure annotated constructor defined for class \"" + configurationClass.getName() + "\". It can only be one");
            }
            Constructor<T> constructor = annotatedConstructors[0];
            final Object[] resolvedParameters = resolveParameters(constructor);
            try {
                constructor.setAccessible(true);
                return constructor.newInstance(resolvedParameters);
            } catch (InvocationTargetException e) {
                throw new ConstrettoException("Could not instantiate class with @Configure annotated constructor");
            }

        }
    }

    private <T> Constructor<T>[] findAnnotatedConstructorsOnClass(final Class<T> configurationClass) {
        return Constructors.findConstructorsWithConfigureAnnotation(configurationClass);
    }

    protected ConfigurationValue findElementOrThrowException(String expression) {
        if (!configuration.containsKey(expression)) {
            throw new ConstrettoExpressionException(expression, currentTags);
        }
        List<ConfigurationValue> values = configuration.get(expression);
        ConfigurationValue resolvedNode = resolveMatch(values);
        if (resolvedNode == null) {
            throw new ConstrettoExpressionException(expression, currentTags);
        }
        if (resolvedNode.value().containsVariables()) {
            for (String key : resolvedNode.value().referencedKeys()) {
                resolvedNode.value().replace(key, evaluateToString(key));
            }
        }
        return resolvedNode;
    }


    protected ConfigurationValue findElementOrNull(String expression) {
        if (!configuration.containsKey(expression)) {
            return null;
        }
        List<ConfigurationValue> values = configuration.get(expression);
        ConfigurationValue resolvedNode = resolveMatch(values);
        if (resolvedNode == null) {
            return null;
        }
        if (resolvedNode.value().containsVariables()) {
            for (String key : resolvedNode.value().referencedKeys()) {
                resolvedNode.value().replace(key, evaluateToString(key));
            }
        }
        return resolvedNode;
    }

    @SuppressWarnings("unchecked")
    private <T> T processAndConvert(Class<T> clazz, String expression) throws ConstrettoException {
        ConfigurationValue value = findElementOrThrowException(expression);
        return (T) ValueConverterRegistry.convert(clazz, clazz, value.value());
    }

    private ConfigurationValue resolveMatch(List<ConfigurationValue> values) {
        ConfigurationValue bestMatch = null;
        for (ConfigurationValue configurationNode : values) {
            if (ConfigurationValue.DEFAULT_TAG.equals(configurationNode.tag())) {
                if (bestMatch == null || bestMatch.tag().equals(ConfigurationValue.DEFAULT_TAG)) {
                    bestMatch = configurationNode;
                }
            } else if (currentTags.contains(configurationNode.tag())) {
                if (bestMatch == null) {
                    bestMatch = configurationNode;
                } else {
                    int previousFoundPriority =
                            ConfigurationValue.DEFAULT_TAG.equals(bestMatch.tag()) ?
                                    Integer.MAX_VALUE : currentTags.indexOf(bestMatch.tag());
                    if (currentTags.indexOf(configurationNode.tag()) <= previousFoundPriority) {
                        bestMatch = configurationNode;
                    }
                }
            } else if (ConfigurationValue.ALL_TAG.equals(configurationNode.tag())) {
                bestMatch = configurationNode;
            }
        }
        return bestMatch;
    }

    private <T> void injectConfiguration(T objectToConfigure) {
        injectFields(objectToConfigure);
        injectMethods(objectToConfigure);
        boolean found = false;
        for (WeakReference<Object> configuredObject : configuredObjects) {
            if (configuredObject.get() == objectToConfigure) {
                found = true;
                break;
            }
        }
        if (!found) {
            this.configuredObjects.add(new WeakReference<Object>(objectToConfigure));
        }
    }

    private Object[] resolveParameters(AccessibleObject accessibleObject) throws IllegalAccessException, InstantiationException {
        Annotation[][] methodAnnotations;
        String[] parameterNames;
        Class<?>[] parameterTargetTypes;

        if(accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            methodAnnotations = method.getParameterAnnotations();
            parameterNames = paranamer.lookupParameterNames(method);
            parameterTargetTypes = method.getParameterTypes();
        } else if(accessibleObject instanceof Constructor) {
            Constructor constructor = (Constructor) accessibleObject;
            methodAnnotations = constructor.getParameterAnnotations();
            parameterNames = paranamer.lookupParameterNames(constructor);
            parameterTargetTypes = constructor.getParameterTypes();
        } else {
            throw new ConstrettoException("Could not resolve parameter names ");
        }

        Object[] resolvedArguments = new Object[methodAnnotations.length];
        int i = 0;
        for (Annotation[] parameterAnnotations : methodAnnotations) {
            Object defaultValue = null;
            boolean required = true;
            String expression = "";
            Class<?> parameterTargetClass = parameterTargetTypes[i];
            if (parameterAnnotations.length != 0) {
                for (Annotation parameterAnnotation : parameterAnnotations) {
                    if (parameterAnnotation.annotationType() == Configuration.class) {
                        Configuration configurationAnnotation = (Configuration) parameterAnnotation;
                        expression = configurationAnnotation.value();
                        required = configurationAnnotation.required();
                        if (hasAnnotationDefaults(configurationAnnotation)) {
                            if (configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class)) {
                                defaultValue = ValueConverterRegistry.convert(parameterTargetClass, parameterTargetClass, new CPrimitive(configurationAnnotation.defaultValue()));
                            } else {
                                ConfigurationDefaultValueFactory valueFactory = configurationAnnotation.defaultValueFactory().newInstance();
                                defaultValue = valueFactory.getDefaultValue();
                            }
                        }
                    }
                }
            }
            if (expression.equals("")) {
                if (parameterNames == null) {
                    throw new ConstrettoException("Could not resolve the expression of the property to look up. " +
                                                          "The cause of this could be that the class is compiled without debug enabled. " +
                                                          "when a class is compiled without debug, the @Configuration with a value attribute is required " +
                                                          "to correctly resolve the property expression.");
                } else {
                    expression = parameterNames[i];
                }
            }
            if (hasValue(expression)) {
                if (parameterTargetClass.isAssignableFrom(List.class)) {
                    Class<?> collectionParameterType = getCollectionParameterType(createMethodParameter(accessibleObject, i));
                    resolvedArguments[i] = evaluateToList(collectionParameterType, expression);
                } else if (parameterTargetClass.isAssignableFrom(Map.class)) {
                    Class<?> mapKeyType = getMapKeyParameterType(createMethodParameter(accessibleObject, i));
                    Class<?> mapValueType = getMapValueParameterType(createMethodParameter(accessibleObject, i));
                    resolvedArguments[i] = evaluateToMap(mapKeyType, mapValueType, expression);
                } else {
                    resolvedArguments[i] = processAndConvert(parameterTargetClass, expression);
                }

            } else {
                if (defaultValue != null || !required) {
                    resolvedArguments[i] = defaultValue;
                } else {
                    if(accessibleObject instanceof Constructor) {
                        Constructor constructor = (Constructor) accessibleObject;
                        throw new ConstrettoException("Missing value or default value for expression [" + expression + "], in annotated constructor in class [" + constructor.getClass().getName() + "], with tags " + currentTags + ".");

                    }
                    else {
                        Method method = (Method) accessibleObject;
                        throw new ConstrettoException("Missing value or default value for expression [" + expression + "], in method [" + method.getName() + "], in class [" + method.getClass().getName() + "], with tags " + currentTags + ".");

                    }
                }
            }

            i++;
        }
        return resolvedArguments;

    }

    private <T> void injectMethods(T objectToConfigure) {
        Method[] methods = objectToConfigure.getClass().getMethods();
        for (Method method : methods) {
            try {
                if (method.isAnnotationPresent(Configure.class)) {
                    Object[] resolvedArguments = resolveParameters(method);
                    method.setAccessible(true);
                    method.invoke(objectToConfigure, resolvedArguments);

                }
            } catch (IllegalAccessException e) {
                throw new ConstrettoException("Cold not invoke method ["
                                                      + method.getName() + "] annotated with @Configured,", e);
            } catch (InvocationTargetException e) {
                throw new ConstrettoException("Cold not invoke method ["
                                                      + method.getName() + "] annotated with @Configured,", e);
            } catch (InstantiationException e) {
                throw new ConstrettoException("Cold not invoke method ["
                                                      + method.getName() + "] annotated with @Configured,", e);
            }
        }
    }

    private <T extends AccessibleObject> MethodParameter createMethodParameter(T accessibleObject, final int parameterIndex) {
        if(accessibleObject instanceof Constructor) {
            return new MethodParameter((Constructor) accessibleObject, parameterIndex);
        } else {
            return new MethodParameter((Method) accessibleObject, parameterIndex);
        }
    }

    private <T> void injectFields(T objectToConfigure) {

        Class objectToConfigureClass = objectToConfigure.getClass();

        do {
            Field[] fields = objectToConfigureClass.getDeclaredFields();
            for (Field field : fields) {
                try {
                    if (field.isAnnotationPresent(Configuration.class)) {
                        Configuration configurationAnnotation = field.getAnnotation(Configuration.class);
                        String expression = "".equals(configurationAnnotation.value()) ? field.getName() : configurationAnnotation.value();
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        if (hasValue(expression)) {
                            ConfigurationValue node = findElementOrThrowException(expression);
                            if (fieldType.isAssignableFrom(List.class)) {
                                field.set(objectToConfigure, evaluateToList(getCollectionFieldType(field), expression));
                            } else if (fieldType.isAssignableFrom(Map.class)) {
                                field.set(objectToConfigure, evaluateToMap(getMapKeyFieldType(field), getMapValueFieldType(field), expression));
                            } else {
                                field.set(objectToConfigure, processAndConvert(fieldType, expression));
                            }
                        } else {
                            if (hasAnnotationDefaults(configurationAnnotation)) {
                                if (configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class)) {
                                    field.set(objectToConfigure, ValueConverterRegistry.convert(fieldType, fieldType, new CPrimitive(configurationAnnotation.defaultValue())));
                                } else {
                                    ConfigurationDefaultValueFactory valueFactory = configurationAnnotation.defaultValueFactory().newInstance();
                                    field.set(objectToConfigure, valueFactory.getDefaultValue());
                                }
                            } else if (configurationAnnotation.required()) {
                                throw new ConstrettoException("Missing value or default value for expression [" + expression + "] for field [" + field.getName() + "], in class [" + objectToConfigure.getClass().getName() + "] with tags " + currentTags + ".");
                            }
                        }
                    } else if (field.isAnnotationPresent(Tags.class)) {
                        field.setAccessible(true);
                        field.set(objectToConfigure, currentTags);
                    }
                } catch (IllegalAccessException e) {
                    throw new ConstrettoException("Cold not inject configuration into field ["
                            + field.getName() + "] annotated with @Configuration, in class [" + objectToConfigure.getClass().getName() + "] with tags " + currentTags, e);
                } catch (InstantiationException e) {
                    throw new ConstrettoException("Cold not inject configuration into field ["
                            + field.getName() + "] annotated with @Configuration, in class [" + objectToConfigure.getClass().getName() + "] with tags " + currentTags, e);
                }
            }
        } while ((objectToConfigureClass = objectToConfigureClass.getSuperclass()) != null);
    }

    private boolean hasAnnotationDefaults(Configuration configurationAnnotation) {
        return !("N/A".equals(configurationAnnotation.defaultValue()) && configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class));
    }
}
