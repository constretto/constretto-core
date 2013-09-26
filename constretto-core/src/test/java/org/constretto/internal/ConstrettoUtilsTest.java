package org.constretto.internal;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author sondre
 */
public class ConstrettoUtilsTest {

    public static final String VALUE_ONE = "value1";
    public static final String VALUE_TWO = "value2";

    @Test
    public void testAsCsv() throws Exception {
        final String csvText = ConstrettoUtils.asCsv(new String[]{VALUE_ONE, VALUE_TWO});
        assertEquals(VALUE_ONE + "," + VALUE_TWO, csvText);
    }

    @Test
    public void testFromCSV() throws Exception {
        final List<String> values = ConstrettoUtils.fromCSV(VALUE_ONE + "," + VALUE_TWO);
        assertEquals(Arrays.asList(VALUE_ONE, VALUE_TWO), values);
    }
}
