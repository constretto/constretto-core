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
package org.constretto.internal.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoException;
import org.constretto.internal.converter.ValueConverterRegistry;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

import static java.lang.System.setProperty;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ValueConversionTest {
    private ConstrettoConfiguration configuration;

    @Before
    public void prepareTests() {
        setProperty("boolean.valid.true", "true");
        setProperty("boolean.valid.false", "false");
        setProperty("boolean.invalid", "this is not a boolean");
        setProperty("nan", "this is not a number");
        setProperty("float.valid", "1.4E-45F");
        setProperty("double.valid", "4.9E-324");
        setProperty("long.valid", "-9223372036854775808");
        setProperty("int.valid", "-2147483648");
        setProperty("short.valid", "-32768");
        setProperty("byte.valid", "-128");
        setProperty("custom.data", "Some data");
        setProperty("locale.valid", "en_US");
        setProperty("locale.invalid", "no_PO");
        setProperty("array.of.strings", "[\"one\",\"two\",\"three\"]");
        setProperty("array.of.ints", "[\"1\",\"2\",\"3\"]");
        setProperty("map.of.int.float", "{\"1\":\"10\",\"2\":\"20\"}");
        configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();
    }

    @Test
    public void evaluateBoolean() {
        assertEquals(Boolean.TRUE, configuration.evaluateToBoolean("boolean.valid.true"));
        assertEquals(Boolean.TRUE, configuration.evaluateTo(Boolean.class, "boolean.valid.true"));
        assertEquals(Boolean.FALSE, configuration.evaluateToBoolean("boolean.valid.false"));
        assertEquals(Boolean.FALSE, configuration.evaluateTo(Boolean.class, "boolean.valid.false"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToBoolean("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Boolean.class, "nan");
            }
        });
    }

    @Test
    public void evaluateFloat() {
        Float f = Float.MIN_VALUE;
        assertEquals(f, configuration.evaluateToFloat("float.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToFloat("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Float.class, "nan");
            }
        });
    }

    @Test
    public void evaluateDouble() {
        Double d = Double.MIN_VALUE;
        assertEquals(d, configuration.evaluateToDouble("double.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToDouble("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Double.class, "nan");
            }
        });
    }

    @Test
    public void evaluateLong() {
        Long l = Long.MIN_VALUE;
        assertEquals(l, configuration.evaluateToLong("long.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToLong("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Long.class, "nan");
            }
        });
    }

    @Test
    public void evaluateInteger() {
        Integer i = Integer.MIN_VALUE;
        assertEquals(i, configuration.evaluateToInt("int.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToInt("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Integer.class, "nan");
            }
        });
    }

    @Test
    public void evaluateByte() {
        Byte b = Byte.MIN_VALUE;
        assertEquals(b, configuration.evaluateToByte("byte.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToByte("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Byte.class, "nan");
            }
        });
    }

    @Test
    public void evaluateShort() {
        Short b = Short.MIN_VALUE;
        assertEquals(b, configuration.evaluateToShort("short.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateToShort("nan");
            }
        });
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Short.class, "nan");
            }
        });
    }

    @Test
    public void evaluateLocale() {
        Locale locale = Locale.US;
        assertEquals(locale, configuration.evaluateTo(Locale.class, "locale.valid"));
        assertException(ConstrettoConversionException.class, new Guard() {
            public void operation() {
                configuration.evaluateTo(Locale.class, "locale.invalid");
            }
        });
    }

    @Test(expected = ConstrettoException.class)
    public void evaluateWithMissingConverter() {
        configuration.evaluateTo(ValueConversionTest.class, "nan");
    }

    @Test
    public void evaluateWithCustomConverter() {
        CustomData customData = new CustomData("Some data");
        ValueConverterRegistry.registerCustomConverter(CustomData.class, new CustomDataValueConverter());
        assertEquals(customData, configuration.evaluateTo(CustomData.class, "custom.data"));
    }

    @Test
    public void evaluateStringArray() {
        List<String> expected = new ArrayList<String>() {{
            add("one");
            add("two");
            add("three");
        }};
        List<String> result = configuration.evaluateToArray(String.class, "array.of.strings");
        assertList(expected, result);
    }

    @Test
    public void evaluateIntegerArray() {
        List<Integer> expected = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};
        List<Integer> result = configuration.evaluateToArray(Integer.class, "array.of.ints");
        assertList(expected, result);
    }

    @Test
    public void evaluateMapOfSimpleTypes() {
        Map<Integer, Float> expected = new HashMap<Integer, Float>() {{
            put(1, 10f);
            put(2, 20f);
        }};
        Map<Integer, Float> result = configuration.evaluateToMap(Integer.class, Float.class, "map.of.int.float");
        assertMap(expected, result);
    }

    //
    // Helper methods
    //
    private void assertList(List<?> expected, List<?> result) {
        assertEquals(expected.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(expected.get(i), result.get(i));
        }
    }

    private void assertMap(Map<?, ?> expected, Map<?, ?> result) {
        assertEquals(expected.size(), result.size());
        for (Object key : expected.keySet()) {
            assertEquals(expected.get(key), result.get(key));
        }
    }

    private void assertException(Class<? extends Throwable> expectedException, Guard guard) {
        try {
            guard.operation();
        } catch (Throwable e) {
            assertEquals(expectedException, e.getClass());
        }
    }

    private interface Guard {
        void operation();
    }

    private class CustomData {
        private String data;

        private CustomData(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CustomData that = (CustomData) o;

            return !(data != null ? !data.equals(that.data) : that.data != null);

        }

        @Override
        public int hashCode() {
            return data != null ? data.hashCode() : 0;
        }
    }

    private class CustomDataValueConverter implements ValueConverter<CustomData> {
        private final Type listType = new TypeToken<List<CustomData>>() {
        }.getType();
        private final Gson gson = new Gson();

        public CustomData fromString(String value) throws ConstrettoConversionException {
            return new CustomData(value);
        }

        public List<CustomData> fromStrings(String value) throws ConstrettoConversionException {
            List<CustomData> customDatas = new ArrayList<CustomData>();
            List<String> strings = gson.fromJson(value, listType);
            for (String string : strings) {
                customDatas.add(fromString(string));
            }
            return customDatas;
        }

        public String toString(CustomData value) {
            return value.getData();
        }
    }

}
