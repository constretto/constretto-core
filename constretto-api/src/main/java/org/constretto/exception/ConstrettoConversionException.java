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
package org.constretto.exception;

/**
 * Thrown when a ValueConverter cannot perform conversion succesfully
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoConversionException extends ConstrettoException {
    private final String value;
    private Class targetClass;


    public ConstrettoConversionException(String value, Class targetClass, Throwable cause) {
        super(cause);
        this.value = value;
        this.targetClass = targetClass;
    }

    public ConstrettoConversionException(String value, Class targetClass, String message) {
        super(message);
        this.value = value;
        this.targetClass = targetClass;
    }

    public String getValue() {
        return value;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    @Override
    public String toString() {
        return "Conversion failed for value \"" + value + "\" to type \"" + targetClass.getName() + "\" with message \"" + getMessage() + "\"";
    }

}