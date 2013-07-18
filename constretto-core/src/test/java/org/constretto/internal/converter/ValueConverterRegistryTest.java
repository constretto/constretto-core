/*
 * Copyright 2011 the original author or authors.
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
import org.constretto.model.CArray;
import org.constretto.model.CObject;
import org.constretto.model.CPrimitive;
import org.constretto.model.CValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author <a href="mailto:asbjorn@aarrestad.com">Asbj&oslash;rn Aarrestad</a>
 */
public class ValueConverterRegistryTest {

	@SuppressWarnings("unchecked")
	@Test
	public void simpleListConversion() {
		final List<CValue> list = new ArrayList<CValue>();
		list.add(new CPrimitive("hei"));
		final List<String> convertedList = (List<String>) ValueConverterRegistry.convert(String.class, String.class, new CArray(list));

		Assert.assertNotNull(convertedList);
		Assert.assertTrue(convertedList.size() == 1);
	}


    @Test
	public void mapListConversion() {
		final List<CValue> list = new ArrayList<CValue>();
		final Map<String, CValue> data = new HashMap<String, CValue>();
		data.put("key", new CPrimitive("value"));
		list.add(new CObject(data));

		final List<Map<String, String>> convertedList = (List<Map<String, String>>) ValueConverterRegistry.convert(String.class,
				String.class, new CArray(list));

		Assert.assertNotNull(convertedList);
		Assert.assertTrue(convertedList.size() == 1);
		final Map<String, String> element = convertedList.get(0);
		Assert.assertTrue(element.size() == 1);
		Assert.assertEquals("value", element.get("key"));
	}

    @Test(expected = ConstrettoException.class)
    public void testIllegalDataTypeConversion() throws Exception {
        ValueConverterRegistry.convert(String.class, String.class, new CValue() {
            @Override
            public Set<String> referencedKeys() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void replace(final String key, final String resolvedValue) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }

    @Test(expected = ConstrettoException.class)
    public void testConvertUnsopportedClassConversion() {
        ValueConverterRegistry.convertMap(getClass(), null, null);
    }
}
