package org.constretto.model;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot at gmail dot com
 */
public class CObjectTest {

    @Test
    public void testAllOperations() throws Exception {
        Map<String, CValue> values = new LinkedHashMap<String, CValue>();
        values.put("key1", new CPrimitive("1"));
        values.put("key2", new CPrimitive("2"));
        final CObject cObject = new CObject(values);
        assertEquals(values, cObject.data());
        assertEquals("{key1:1, key2:2}", cObject.toString());


    }
}
