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

import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoException;
import org.constretto.exception.ConstrettoExpressionException;
import static org.constretto.internal.converter.ValueConverterRegistry.convert;
import org.constretto.model.ConfigurationElement;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultConstrettoConfiguration implements ConstrettoConfiguration {
    private final ConfigurationElement configuration;

    public DefaultConstrettoConfiguration(ConfigurationElement configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public <K> K evaluate(String expression, K defaultValue) {
        ConfigurationElement element = configuration.find(expression);
        K value;
        try {
            value = (K) convert(defaultValue.getClass(), element.getValue());
        } catch (ConstrettoConversionException e) {
            value = null;
        }
        return null != value ? value : defaultValue;
    }

    public <K> K evaluateTo(Class<K> targetClass, String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(targetClass, element.getValue());
    }

    public String evaluateToString(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return element.getValue();
    }

    public Boolean evaluateToBoolean(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Boolean.class, element.getValue());
    }

    public Double evaluateToDouble(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Double.class, element.getValue());
    }

    public Long evaluateToLong(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Long.class, element.getValue());
    }

    public Float evaluateToFloat(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Float.class, element.getValue());
    }

    public Integer evaluateToInt(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Integer.class, element.getValue());
    }

    public Short evaluateToShort(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Short.class, element.getValue());
    }

    public Byte evaluateToByte(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return convert(Byte.class, element.getValue());
    }

    public <T> T as(Class<T> configurationClass) throws ConstrettoException {
        T objectToConfigure;
        try {
            objectToConfigure = configurationClass.newInstance();
        } catch (Exception e) {
            throw new ConstrettoException("Could not instansiate class of type: " + configurationClass.getName()
                    + " when trying to inject it with configuration", e);
        }

        injectConfigurationUsingReflection(objectToConfigure);

        return objectToConfigure;
    }

    public <T> T applyOn(T objectToConfigure) throws ConstrettoException {
        injectConfigurationUsingReflection(objectToConfigure);
        return objectToConfigure;
    }

    public ConstrettoConfiguration at(String expression) {
        ConfigurationElement currentConfigurationElement = configuration.find(expression);
        if (null == currentConfigurationElement) {
            throw new ConstrettoExpressionException(expression, "Expression not found");
        }
        return new DefaultConstrettoConfiguration(currentConfigurationElement);
    }

    public boolean hasValue(String expression) {
        ConfigurationElement element = configuration.find(expression);
        return null != element;
    }


    //
    // Helper methods
    //
    private ConfigurationElement findElementOrThrowException(String expression) {
        ConfigurationElement element = configuration.find(expression);
        if (null == element) {
            throw new ConstrettoExpressionException(expression, "expression not found in configuration");
        }
        return element;
    }

    private <T> void injectConfigurationUsingReflection(T objectToConfigure) {
        Field[] fields = objectToConfigure.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            ConfigurationElement element = findElementOrThrowException(name);
            try {
                field.set(objectToConfigure, convert(fieldType, element.getValue()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
