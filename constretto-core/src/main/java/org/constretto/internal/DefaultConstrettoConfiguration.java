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

import java.lang.reflect.Field;

import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoException;
import org.constretto.exception.ConstrettoExpressionException;
import org.constretto.internal.converter.ConstrettoValueConverter;
import org.constretto.model.ConfigurationElement;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultConstrettoConfiguration implements ConstrettoConfiguration {
    private final ConfigurationElement configuration;

    public DefaultConstrettoConfiguration(ConfigurationElement configuration) {
        this.configuration = configuration;
    }

    public <T> T applyOn(T objectToConfigure) throws ConstrettoException {
        injectConfigurationWithReflection(objectToConfigure);
        return objectToConfigure;
    }

    public <T> T as(Class<T> configurationClass) throws ConstrettoException {
        T objectToConfigure;
        try {
            objectToConfigure = configurationClass.newInstance();
        } catch (Exception e) {
            throw new ConstrettoException("Could not instansiate class of type: " + configurationClass.getName()
                    + " when trying to inject it with configuration", e);
        }

        injectConfigurationWithReflection(objectToConfigure);

        return objectToConfigure;
    }

    public ConstrettoConfiguration at(String expression) {
        ConfigurationElement currentConfigurationElement = configuration.find(expression);
        if (null == currentConfigurationElement) {
            throw new ConstrettoExpressionException(expression, "Expression not found");
        }
        return new DefaultConstrettoConfiguration(currentConfigurationElement);
    }

    @SuppressWarnings("unchecked")
    public <K> K evaluate(String expression, K defaultValue) {
        ConfigurationElement element = configuration.find(expression);
        return null != element ? (K) ConstrettoValueConverter.convert(defaultValue.getClass(), element.getValue()) : defaultValue;
    }

    public boolean evaluateToBoolean(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return ConstrettoValueConverter.convert(Boolean.class, element.getValue());
    }

    public byte evaluateToByte(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public double evaluateToDouble(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public float evaluateToFloat(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public int evaluateToInt(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public long evaluateToLong(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public short evaluateToShort(String expression) throws ConstrettoExpressionException {
        return 0;
    }

    public String evaluateToString(String expression) throws ConstrettoExpressionException {
        ConfigurationElement element = findElementOrThrowException(expression);
        return element.getValue();
    }

    private ConfigurationElement findElementOrThrowException(String expression) {
        ConfigurationElement element = configuration.find(expression);
        if (null == element) {
            throw new ConstrettoExpressionException(expression, "expression not found in configuration");
        }
        return element;
    }

    public <T> T get(String key, Class<T> configuredClass) throws ConstrettoExpressionException {
        return null;
    }

    public <T> T get(String key, T defaultValue, Class<T> configuredClass) {
        return null;
    }

    public boolean hasValue(String key) {
        return false;
    }

    private <T> void injectConfigurationWithReflection(T objectToConfigure) {
        Field[] fields = objectToConfigure.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            ConfigurationElement element = findElementOrThrowException(name);
            try {
                field.set(objectToConfigure, ConstrettoValueConverter.convert(fieldType, element.getValue()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
