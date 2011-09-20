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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class BooleanValueConverter implements ValueConverter<Boolean> {
    private final Type listType = new TypeToken<List<Boolean>>() {}.getType();
    private final Gson gson = new Gson();

    private static Set<String> validStrings = new HashSet<String>() {{
        add("true");
        add("false");
    }};

    public Boolean fromString(String value) throws ConstrettoConversionException {
        if (!validStrings.contains(value.toLowerCase())) {
            throw new ConstrettoConversionException(value, Boolean.class, "valid values are \"true\" and \"false\" ignoring case.");
        }
        return Boolean.valueOf(value);
    }

    public List<Boolean> fromStrings(String value) throws ConstrettoConversionException {
        return gson.fromJson(value, listType);
    }
}
