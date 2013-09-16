package org.constretto;

import org.constretto.model.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for issue reported by jhberges concerning stripping of leading chars from properties.
 *
 * @author zapodot at gmail dot com
 * @see <a href="https://github.com/constretto/constretto-core/issues/36">Issue 36</a>
 */
public class LeadingCharsTest {

    private ConstrettoConfiguration constrettoConfiguration;

    @Before
    public void setUp() throws Exception {
        this.constrettoConfiguration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(Resource.create("classpath:leading-chars-strip.properties"))
                .done()
                .getConfiguration();

    }

    @Test
    public void testLeadingZeros() throws Exception {
        assertEquals("0051", constrettoConfiguration.evaluateToString("leading.zero"));

    }

    @Test
    public void testLeadingPlus() throws Exception {
        assertEquals("+47", constrettoConfiguration.evaluateToString("leading.plus"));

    }
}
