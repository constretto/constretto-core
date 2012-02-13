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

import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.internal.ConstrettoUtils;

import java.util.Locale;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class LocaleValueConverter implements ValueConverter<Locale> {
    public Locale fromString(String value) throws ConstrettoConversionException {
        try {
            return ConstrettoUtils.toLocale(value);
        } catch (IllegalArgumentException e) {
            throw new ConstrettoConversionException(value, Locale.class, e);
        }
    }
}