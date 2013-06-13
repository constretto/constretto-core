package org.constretto.test;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Tags;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * This source code is the property of NextGenTel AS
 *
 * @author sek
 */
@Tags({"purejunit", "test"})
public class ConstrettoRuleClassRuleTest {

    @Tags
    List<String> currentEnvironment;

    @ClassRule
    public static ConstrettoRule constrettoRule = new ConstrettoRule();

    @Test
    public void testAsClassRule() throws Exception {
        String[] expected = {"purejunit", "test"};

        ConstrettoConfiguration configuration =
                new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();
        configuration.on(this);

        assertArrayEquals(expected, currentEnvironment.toArray(new String[0]));


    }
}
