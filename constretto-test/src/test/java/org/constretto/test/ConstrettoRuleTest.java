/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.constretto.test;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Tags;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit test of {@link ConstrettoRule}.
 *
 * @author <a href="mailto:from.github@nisgits.net">Stig Kleppe-Jorgensen</a>, 2013.01.14
 */
@Tags({"purejunit", "test"})
public class ConstrettoRuleTest {
    @Rule
    public ConstrettoRule constrettoRule = new ConstrettoRule();

    @Tags
    List<String> currentEnvironment;

    @Test
    public void givenEnvironmentAnnotationOnTestClassWhenRunningTestThenConstrettoKnowsEnvironment() {
        String[] expected = {"purejunit", "test"};

        ConstrettoConfiguration configuration =
            new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();
        configuration.on(this);

        assertArrayEquals(expected, currentEnvironment.toArray(new String[0]));
    }
}
