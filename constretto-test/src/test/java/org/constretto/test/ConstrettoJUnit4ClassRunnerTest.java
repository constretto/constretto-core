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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
@Tags( { "purejunit", "test" })
@RunWith(ConstrettoJUnit4ClassRunner.class)
public class ConstrettoJUnit4ClassRunnerTest {

    @Tags
    String[] currentEnvironment;

    @Test
    public void givenEnvironmentAnnotationOnTestClassWhenRunningTestThenConstrettoKnowsEnvironment() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();
        configuration.on(this);
        String[] expected = { "purejunit", "test" };
        Assert.assertArrayEquals(expected, currentEnvironment);
    }

}
