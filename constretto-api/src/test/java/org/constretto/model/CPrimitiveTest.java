package org.constretto.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class CPrimitiveTest {
    @Test
    public void replace() throws Exception {
        final String key = "my-key";
        final CPrimitive primitive = new CPrimitive("#{" + key + "}");
        final String expectedValue = "C:\\temp\\";

        primitive.replace(key, expectedValue);

        assertEquals(expectedValue, primitive.value());
    }
}
