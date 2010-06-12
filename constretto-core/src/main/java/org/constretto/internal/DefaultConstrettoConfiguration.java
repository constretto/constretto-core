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

import org.constretto.ConfigurationDefaultValueFactory;
import org.constretto.ConstrettoConfiguration;
import org.constretto.Property;
import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;
import org.constretto.annotation.Tags;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoException;
import org.constretto.exception.ConstrettoExpressionException;
import org.constretto.internal.converter.ValueConverterRegistry;
import org.constretto.model.ConfigurationNode;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultConstrettoConfiguration implements ConstrettoConfiguration {
    private static final String NULL_STRING = "![![Null]!]!";
    private static final String VARIABLE_PREFIX = "#{";
    private static final String VARIABLE_SUFFIX = "}";
    private List<String> currentTags;
    private final ConfigurationNode configuration;
    private LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private Set<WeakReference<Object>> configuredObjects = new HashSet<WeakReference<Object>>();

    public DefaultConstrettoConfiguration(ConfigurationNode configuration, List<String> currentTags) {
        this.configuration = configuration;
        this.currentTags = currentTags;
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
            objectToConfigure = configurationClass.newInstance();
        } catch (Exception e) {
            throw new ConstrettoException("Could not instansiate class of type: " + configurationClass.getName()
                    + " when trying to inject it with configuration, It may be missing a default constructor", e);
        }

        injectConfiguration(objectToConfigure);

        return objectToConfigure;
    }

    public <T> T on(T objectToConfigure) throws ConstrettoException {
        injectConfiguration(objectToConfigure);
        return objectToConfigure;
    }

    public ConstrettoConfiguration at(String expression) throws ConstrettoException {
        ConfigurationNode currentConfigurationNode = findElementOrThrowException(expression);
        ConfigurationNode.createRootElementOf(currentConfigurationNode);
        return new DefaultConstrettoConfiguration(currentConfigurationNode, currentTags);
    }

    public ConstrettoConfiguration from(String expression) throws ConstrettoException {
        return at(expression);
    }

    public boolean hasValue(String expression) {
        ConfigurationNode node = findElementOrNull(expression);
        return null != node;
    }

    public void addTag(String... newtags) {
        currentTags.addAll(Arrays.asList(newtags));
        reconfigure();
    }

    public void removeTag(String... newTags) {
        for (String newTag : newTags) {
            currentTags.remove(newTag);
        }
        reconfigure();
    }

    public Iterator<Property> iterator() {
        List<Property> properties = new ArrayList<Property>();
        Map<String, String> map = asMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            properties.add(new Property(entry.getKey(), entry.getValue()));
        }
        return properties.iterator();
    }

    private Map<String, String> asMap() {
        Map<String, String> properties = new HashMap<String, String>();
        extractProperties(configuration, properties);
        return properties;
    }

    private void extractProperties(ConfigurationNode currentNode, Map<String, String> properties) {
        String value = evaluateTo(currentNode.getExpression(), NULL_STRING);
        if (!value.equals(NULL_STRING)) {
            properties.put(currentNode.getExpression(), value);
        }
        if (currentNode.hasChildren()) {
            for (ConfigurationNode child : currentNode.children()) {
                extractProperties(child, properties);
            }
        }
    }

    //
    // Helper methods
    //

    private void reconfigure() {
        WeakReference[] references = configuredObjects.toArray(new WeakReference[configuredObjects.size()]);
        for (WeakReference reference : references) {
            if (reference != null && reference.get() != null) {
                on(reference.get());
            }
        }
    }

    private ConfigurationNode findElementOrThrowException(String expression) {
        List<ConfigurationNode> node = configuration.findAllBy(expression);
        ConfigurationNode resolvedNode = resolveMatch(node);
        if (resolvedNode == null) {
            throw new ConstrettoExpressionException(expression, currentTags);
        }
        return resolvedNode;
    }

    private <T> T processAndConvert(Class<T> clazz, String expression) throws ConstrettoException {
        String parsedValue = processVariablesInProperty(expression, new ArrayList<String>());
        return ValueConverterRegistry.convert(clazz, parsedValue);
    }

    private ConfigurationNode findElementOrNull(String expression) {
        List<ConfigurationNode> node = configuration.findAllBy(expression);
        return resolveMatch(node);
    }

    private ConfigurationNode resolveMatch(List<ConfigurationNode> node) {
        ConfigurationNode bestMatch = null;
        for (ConfigurationNode configurationNode : node) {
            if (ConfigurationNode.DEFAULT_TAG.equals(configurationNode.getTag())) {
                if (bestMatch == null || bestMatch.getTag().equals(ConfigurationNode.DEFAULT_TAG)) {
                    bestMatch = configurationNode;
                }
            } else if (currentTags.contains(configurationNode.getTag())) {
                if (bestMatch == null) {
                    bestMatch = configurationNode;
                } else {
                    int previousFoundPriority =
                            ConfigurationNode.DEFAULT_TAG.equals(bestMatch.getTag()) ?
                                    Integer.MAX_VALUE : currentTags.indexOf(bestMatch.getTag());
                    if (currentTags.indexOf(configurationNode.getTag()) <= previousFoundPriority) {
                        bestMatch = configurationNode;
                    }
                }
            } else if (ConfigurationNode.ALL_TAG.equals(configurationNode.getTag())) {
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

    private <T> void injectMethods(T objectToConfigure) {
        Method[] methods = objectToConfigure.getClass().getMethods();
        for (Method method : methods) {
            try {
                if (method.isAnnotationPresent(Configure.class)) {
                    Annotation[][] methodAnnotations = method.getParameterAnnotations();
                    String[] parameterNames = nameDiscoverer.getParameterNames(method);
                    Object[] resolvedArguments = new Object[methodAnnotations.length];
                    int i = 0;
                    Object defaultValue = null;
                    boolean required = true;
                    for (Annotation[] parameterAnnotations : methodAnnotations) {
                        String expression = "";
                        Class<?> parameterTargetClass = method.getParameterTypes()[i];
                        if (parameterAnnotations.length != 0) {
                            for (Annotation parameterAnnotation : parameterAnnotations) {
                                if (parameterAnnotation.annotationType() == Configuration.class) {
                                    Configuration configurationAnnotation = (Configuration) parameterAnnotation;
                                    expression = configurationAnnotation.expression();
                                    required = configurationAnnotation.required();
                                    if (hasAnnotationDefaults(configurationAnnotation)) {
                                        if (configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class)) {
                                            defaultValue = ValueConverterRegistry.convert(parameterTargetClass, configurationAnnotation.defaultValue());
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
                                        "when a class is compiled without debug, the @Configuration with a expression attribute is required " +
                                        "to correctly resolve the property expression.");
                            } else {
                                expression = parameterNames[i];
                            }
                        }
                        if (hasValue(expression)) {
                            ConfigurationNode node = findElementOrThrowException(expression);
                            resolvedArguments[i] = processAndConvert(parameterTargetClass, node.getExpression());
                        } else {
                            if (defaultValue != null || !required) {
                                resolvedArguments[i] = defaultValue;
                            } else {
                                throw new ConstrettoException("Missing value or default value for expression [" + expression + "], in method [" + method.getName() + "], in class [" + objectToConfigure.getClass().getName() + "], with tags " + currentTags + ".");
                            }
                        }

                        i++;
                    }

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

    private <T> void injectFields(T objectToConfigure) {

        Field[] fields = objectToConfigure.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.isAnnotationPresent(Configuration.class)) {
                    Configuration configurationAnnotation = field.getAnnotation(Configuration.class);
                    String expression = "".equals(configurationAnnotation.expression()) ? field.getName() : configurationAnnotation.expression();
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (hasValue(expression)) {
                        ConfigurationNode node = findElementOrThrowException(expression);
                        field.set(objectToConfigure, processAndConvert(fieldType, node.getExpression()));
                    } else {
                        if (hasAnnotationDefaults(configurationAnnotation)) {
                            if (configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class)) {
                                field.set(objectToConfigure, ValueConverterRegistry.convert(fieldType, configurationAnnotation.defaultValue()));
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

    }

    private boolean hasAnnotationDefaults(Configuration configurationAnnotation) {
        return !("N/A".equals(configurationAnnotation.defaultValue()) && configurationAnnotation.defaultValueFactory().equals(Configuration.EmptyValueFactory.class));
    }

    private String processVariablesInProperty(final String expression, final Collection<String> visitedPlaceholders) {
        visitedPlaceholders.add(expression);
        ConfigurationNode currentNode = findElementOrThrowException(expression);

        String value = currentNode.getValue();
        if (valueNeedsVariableResolving(value)) {
            value = substituteVariablesinValue(value, visitedPlaceholders);
        }
        return value;
    }

    private String substituteVariablesinValue(String value, final Collection<String> visitedPlaceholders) {
        while (valueNeedsVariableResolving(value)) {
            ConfigurationVariable expresionToLookup = extractConfigurationVariable(value);
            if (visitedPlaceholders.contains(expresionToLookup.expression)) {
                throw new ConstrettoException(
                        "A cyclic dependency found in a property");
            }
            DefaultConstrettoConfiguration rootConfig = new DefaultConstrettoConfiguration(configuration.root(), currentTags);

            value = value.substring(0, expresionToLookup.startIndex)
                    + rootConfig.processVariablesInProperty(expresionToLookup.expression, visitedPlaceholders)
                    + value.subSequence(expresionToLookup.endIndex + 1, value.length());
        }
        return value;
    }

    private ConfigurationVariable extractConfigurationVariable(String expression) {
        int startIndex = expression.indexOf(VARIABLE_PREFIX);
        int endindex = expression.indexOf(VARIABLE_SUFFIX, startIndex);
        String parsedExpression = expression.substring(startIndex + 2, endindex);
        return new ConfigurationVariable(startIndex, endindex, parsedExpression);
    }

    private boolean valueNeedsVariableResolving(String value) {
        return null != value && value.contains(VARIABLE_PREFIX) && value.contains(VARIABLE_SUFFIX);
    }

    private static class ConfigurationVariable {
        private final int startIndex;
        private final int endIndex;
        private final String expression;

        public ConfigurationVariable(int startIndex, int endIndex, String expression) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.expression = expression;
        }

        @Override
        public String toString() {
            return expression + ", at: " + startIndex + " to: " + endIndex;
        }
    }
}
