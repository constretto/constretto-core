package org.constretto.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ConfigurationValueTest {

    public static final String SIMPLE_VALUE = "value";

    @Test
    public void testNoTagGiven() throws Exception {
        assertEquals(ConfigurationValue.DEFAULT_TAG, new ConfigurationValue(new CPrimitive(SIMPLE_VALUE)).tag());

    }

    @Test
    public void testValue() throws Exception {
        assertEquals(SIMPLE_VALUE, new ConfigurationValue(new CPrimitive(SIMPLE_VALUE)).value().toString());
    }

    @Test
    public void testTag() throws Exception {
        final String tag = "tag";
        assertEquals(tag, new ConfigurationValue(new CPrimitive(SIMPLE_VALUE), tag).tag());
    }

    @Test
    public void testToString() throws Exception {
        final String stringDescription = new ConfigurationValue(null, null).toString();
        assertEquals(3, stringDescription.split("null").length);
    }
}
