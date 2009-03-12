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
package org.constretto.internal.converter;

import org.constretto.exception.ConstrettoConversionException;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class LongValueConverter implements ValueConverter<Long> {

    public Long fromString(String value) throws ConstrettoConversionException {
        try {
            return Long.decode(value);
        } catch (NumberFormatException e) {
            throw new ConstrettoConversionException(value, Long.class, e);
        }
    }

    public String toString(Long value) {
        return value.toString();
    }
}