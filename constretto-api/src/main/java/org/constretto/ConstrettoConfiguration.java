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
package org.constretto;

import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoException;
import org.constretto.exception.ConstrettoExpressionException;
import org.constretto.model.CValue;

import java.util.List;
import java.util.Map;

/**
 * Client interface.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public interface ConstrettoConfiguration extends Iterable<Property> {

    /**
     * Looks up an expression in the configuration.
     *
     * @param expression   The expression to look up
     * @param defaultValue The value to return of no value found for the expression
     * @param <K>          The target Type
     * @return The converted value for the expression, or the passed default value if expression not found, or conversion error occured.
     * @throws ConstrettoExpressionException If the key is not found
     * @throws ConstrettoConversionException If a valid converter is not found for the target Type
     */
    <K> K evaluateTo(String expression, K defaultValue) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Looks up an expression in the configuration.
     *
     * @param targetClass the class to convert the value
     * @param expression  the expression to look up
     * @param <K>         the target Type
     * @return The converted value for the expression.
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    <K> K evaluateTo(Class<K> targetClass, String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Looks up an expression in the configuration.
     * <p/>
     * Will then instead of using the more simple conversion rules defined
     * for the other methods in the API, allow the client to supply
     * it's own converter for the Data.
     *
     * @param converter  your own custom converter
     * @param expression the expression to lookup
     * @param <T>        the target type for conversion
     * @return The converted value from the custom converter
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     * @since 2.0
     */
    <T> T evaluateWith(GenericConverter<T> converter, String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Looks up an expression in the configuration.
     *
     * @param expression the expression to lookup
     * @return the raw constretto model representation of the configuration value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     * @since 2.0
     */
    CValue evaluate(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Looks up an expression in the configuration.
     * <p/>
     * Uses json array syntax for the value, and will return a list of each element
     * using a converter for the target class.
     * <p/>
     * This method works best on arrays with json primitives, not json objects.
     * If you need more complex array parsing use evaluateWith and create your own custom parser instead.
     *
     * @param targetClass the type for each element in the array
     * @param expression  the expression to lookup
     * @param <K>         the target type for conversion
     * @return list with each element converted.
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     * @since 2.0
     */
    <K> List<K> evaluateToList(Class<K> targetClass, String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Looks up an expression in the configuration.
     * <p/>
     * Uses json object syntax for the value, and will return a Map representing the json structure.
     * using a converter for both the key and value classes.
     * <p/>
     * This method works best on maps with simple key/value pairs where all the keys and all the values have the same type representation.
     * If you need more complex object parsing use evaluateWith and create your own custom parser instead.
     *
     * @param keyClass   the type for the keys
     * @param valueClass the type for the values
     * @param expression the expression to lookup
     * @param <K>        the target type for conversion
     * @return list with each element converted.
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     * @since 2.0
     */
    <K, V> Map<K, V> evaluateToMap(Class<K> keyClass, Class<V> valueClass, String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(String.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    String evaluateToString(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Boolean.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Boolean evaluateToBoolean(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Double.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Double evaluateToDouble(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Long.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Long evaluateToLong(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Float.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Float evaluateToFloat(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Integer.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Integer evaluateToInt(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Short.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Short evaluateToShort(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Alias for evaluateTo(Byte.class,expression)
     *
     * @param expression the expression to lookup
     * @return The converted value
     * @throws ConstrettoExpressionException if the expression is malformed, or a value not found for the expression
     * @throws ConstrettoConversionException If a conversion error occurs for the resolved value
     */
    Byte evaluateToByte(String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    /**
     * Examines if an expression exists in the current environment
     *
     * @param expression the expression to lookup
     * @return true if found for current env
     */
    boolean hasValue(String expression);

    /**
     * Will instantiate a given class by reflection, and inject with configuration.
     * <p/>
     * The class will need to have a default constructor.
     *
     * @param configurationClass the class to instantiate
     * @param <T>                the target type
     * @return new and fully configured object.
     * @throws ConstrettoException If a conversion error occurs for resolved values
     */
    <T> T as(Class<T> configurationClass) throws ConstrettoException;

    /**
     * Will inject with configuration to any java object.
     * Will look for methods annotated with @Configure, and
     * fields annotated with @Configuration.
     *
     * @param objectToConfigure the object to inject configuration
     * @param <T>               the object type
     * @return Fully configured object.
     * @throws ConstrettoException If a conversion error occurs for resolved values
     */
    <T> T on(T objectToConfigure) throws ConstrettoException;

    /**
     * Will shuffle the configuration values to an instance of map.
     * This method requires you to have configured the required tags to resolve all properties in the configuration.
     *
     * @return a populated instance extending the {@link java.util.Map} interface
     * @throws ConstrettoException If some values are not qualifiable by the configured tags
     */
    Map<String, String> asMap();

}
