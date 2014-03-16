package org.constretto.model;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot at gmail dot com
 */
public class CObjectTest {

    private CObject cObject;
    private Map<String,CValue> values;

    @Before
    public void setUp() throws Exception {
        values = new LinkedHashMap<String, CValue>();
        values.put("key1", new CPrimitive("1"));
        values.put("key2", new CPrimitive("2"));
        this.cObject = new CObject(values);

    }

    @Test
    public void testAllOperations() throws Exception {

        final CObject cObject = this.cObject;
        assertEquals(values, cObject.data());
        assertEquals("{key1:1, key2:2}", cObject.toString());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(cObject, new CObject(values));

    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(cObject.hashCode(), new CObject(values).hashCode());

    }
}
