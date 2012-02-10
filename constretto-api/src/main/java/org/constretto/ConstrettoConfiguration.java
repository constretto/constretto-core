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
     * @throws ConstrettoConversionException If a conversion error occures for the resolved value
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
     * @since 2.0
     */
    <T> T evaluateWith(GenericConverter<T> converter, String expression) throws ConstrettoExpressionException, ConstrettoConversionException;

    String evaluateToString(String expression) throws ConstrettoException;

    Boolean evaluateToBoolean(String expression) throws ConstrettoException;

    Double evaluateToDouble(String expression) throws ConstrettoException;

    Long evaluateToLong(String expression) throws ConstrettoException;

    Float evaluateToFloat(String expression) throws ConstrettoException;

    Integer evaluateToInt(String expression) throws ConstrettoException;

    Short evaluateToShort(String expression) throws ConstrettoException;

    Byte evaluateToByte(String expression) throws ConstrettoException;

    <K> List<K> evaluateToList(Class<K> targetClass, String expression);

    <K, V> Map<K, V> evaluateToMap(Class<K> keyClass, Class<V> valueClass, String expression);

    <T> T as(Class<T> configurationClass) throws ConstrettoException;

    <T> T on(T objectToConfigure) throws ConstrettoException;

    boolean hasValue(String expression) throws ConstrettoException;

    void appendTag(String... newtag) throws ConstrettoException;

    void prependTag(String... newtag) throws ConstrettoException;

    void removeTag(String... newTag) throws ConstrettoException;

    /**
     * Resets all tags in Constretto to the ones originally
     * configured either with a ConfigurationContextResolver, or
     * by the ConstrettoBuilder class.
     *
     * @param reconfigure if set constretto will run the reconfigure() method after the reset.
     *                    Note this may result in exceptions from constretto if default values does not exist for all keys
     *                    injected in methods or fields annotated with @Configure or @Configuration
     */
    void resetTags(boolean reconfigure) throws ConstrettoException;

    /**
     * Clears all tags in Constretto including the ones originally
     * configured either with a ConfigurationContextResolver, or
     * by the ConstrettoBuilder class. Resulting in Constretto having
     * no configuration tags registered.
     * <p/>
     * This is a non recoverable operation and after use you will need to build your tags from scratch.
     *
     * @param reconfigure if set constretto will run the reconfigure() method after clearing.
     *                    Note this may result in exceptions from constretto if default values does not exist for all keys
     *                    injected in methods or fields annotated with @Configure or @Configuration
     */
    void clearTags(boolean reconfigure) throws ConstrettoException;

    List<String> getCurrentTags();


    /**
     * Will rerun all @Configure annotated methods with the current configuration.
     */
    void reconfigure() throws ConstrettoException;
}
