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

import org.constretto.exception.ConstrettoException;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ValueConverterRegistry {
    private static final Map<Class<?>, ValueConverter<?>> converters = new HashMap<Class<?>, ValueConverter<?>>() {
        {
            put(Boolean.class, new BooleanValueConverter());
            put(boolean.class, new BooleanValueConverter());
            put(Float.class, new FloatValueConverter());
            put(float.class, new FloatValueConverter());
            put(Double.class, new DoubleValueConverter());
            put(double.class, new DoubleValueConverter());
            put(Long.class, new LongValueConverter());
            put(long.class, new LongValueConverter());
            put(Integer.class, new IntegerValueConverter());
            put(int.class, new IntegerValueConverter());
            put(Byte.class, new ByteValueConverter());
            put(byte.class, new ByteValueConverter());
            put(Short.class, new ShortValueConverter());
            put(short.class, new ShortValueConverter());
            put(String.class, new StringValueConverter());
            put(Resource.class, new SpringResourceValueConverter());
            put(File.class, new FileValueConverter());
            put(Locale.class, new LocaleValueConverter());
            put(Properties.class, new PropertyFileValueConverter());
            put(InputStreamValueConverter.class, new InputStreamValueConverter());
        }
    };

    public static void registerCustomConverter(Class<?> converterFor, ValueConverter<?> converter) {
        converters.put(converterFor, converter);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> clazz, String value) throws ConstrettoException {

        if (!converters.containsKey(clazz)) {
            throw new ConstrettoException("No converter found for class: " + clazz.getName());
        }
        ValueConverter<?> converter = converters.get(clazz);
        return (T) converter.fromString(value);
    }

}
