package org.constretto.model;

import org.junit.Test;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class TaggedPropertySetTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullTags() throws Exception {
        new TaggedPropertySet(null, null, null);

    }
}
