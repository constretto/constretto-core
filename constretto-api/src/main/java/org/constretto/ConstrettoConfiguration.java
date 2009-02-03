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

import org.constretto.exception.ConstrettoException;

/**
 * Client interface.
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public interface ConstrettoConfiguration {

    String evaluateToString(String expression) throws ConstrettoException;

    int evaluateToInt(String expression) throws ConstrettoException;

    float evaluateToFloat(String expression) throws ConstrettoException;

    boolean evaluateToBoolean(String expression) throws ConstrettoException;

    double evaluateToDouble(String expression) throws ConstrettoException;

    byte evaluateToByte(String expression) throws ConstrettoException;

    short evaluateToShort(String expression) throws ConstrettoException;

    long evaluateToLong(String expression) throws ConstrettoException;

    <K> K evaluate(String expression, K defaultValue);

    ConstrettoConfiguration at(String expression);

    <T> T as(Class<T> configurationClass) throws ConstrettoException;

    <T> T applyOn(T objectToConfigure) throws ConstrettoException;

    <T> T get(String key, Class<T> configuredClass) throws ConstrettoException;

    <T> T get(String key, T defaultValue, Class<T> configuredClass);

    boolean hasValue(String key);
}
