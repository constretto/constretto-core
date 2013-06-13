/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.provider.helper.ContagiousConfigurationMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultValuesInAnnotationsTest {

    @Test
    public void defaultValuesShouldNotBeContagious() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder().getConfiguration();
        ContagiousConfigurationMethod testObject = configuration.as(ContagiousConfigurationMethod.class);
        assertEquals("default-value", testObject.valueWithDefault());
        assertEquals(null, testObject.missingValue());
        assertEquals(null, testObject.otherMissingValue());
    }
}
