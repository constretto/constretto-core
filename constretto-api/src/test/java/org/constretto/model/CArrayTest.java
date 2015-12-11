package org.constretto.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author zapodot at gmail dot com
 */
public class CArrayTest {

    public static final String VALUE_ONE = "1";
    public static final String VALUE_TWO = "2";
    public static final CPrimitive PRIMITIVE_ONE = new CPrimitive(VALUE_ONE);
    public static final CPrimitive PRIMITIVE_TWO = new CPrimitive(VALUE_TWO);
    private CArray cArray;

    @Before
    public void setUp() throws Exception {
        cArray = new CArray(Arrays.<CValue>asList(PRIMITIVE_ONE, PRIMITIVE_TWO));
    }

    @Test
    public void testData() throws Exception {
        assertArrayEquals(new CValue[]{PRIMITIVE_ONE, PRIMITIVE_TWO}, cArray.data().toArray(new CValue[]{}));
    }

    @Test
    public void testReferencedKeys() throws Exception {
        assertEquals(false, cArray.referencedKeys().iterator().hasNext());
    }

    @Test(expected = NullPointerException.class)
    public void testNull() throws Exception {
        new CArray(null);
    }


    @Test
    public void testReplace() throws Exception {
        final CArray arrayWithKey = new CArray(Arrays.<CValue>asList(new CPrimitive("#{key}")));

        ArrayList<String> objects = new ArrayList<String>();
        for (String s : arrayWithKey.referencedKeys()) {
            objects.add(s);
        }

        assertEquals(1, objects.size());
        arrayWithKey.replace("key", VALUE_ONE);
        assertArrayEquals(new CValue[]{PRIMITIVE_ONE}, arrayWithKey.data().toArray(new CValue[]{}));


    }

    @Test
    public void testToString() throws Exception {

        assertEquals("[" + VALUE_ONE + "," + VALUE_TWO + "]", cArray.toString());
    }
}
