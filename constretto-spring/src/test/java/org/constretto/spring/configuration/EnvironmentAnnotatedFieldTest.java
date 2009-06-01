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
package org.constretto.spring.configuration;

import static junit.framework.Assert.assertEquals;
import org.constretto.spring.annotation.Environment;
import org.constretto.internal.provider.ConfigurationProvider;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.assembly.helper.AlwaysDevelopmentEnvironmentResolver;
import static org.constretto.spring.configuration.EnvironmentAnnotatedFieldTest.MyEnvironments.development;
import org.junit.Test;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class EnvironmentAnnotatedFieldTest {

    @Test
    public void givenClassWithEnvironmentAnnotatedPropertyThenInjectEnvironment() throws Exception {
        TestClazz testClazz = new TestClazz();
        ConfigurationAnnotationConfigurer annotationConfigurer = new ConfigurationAnnotationConfigurer(
                new ConfigurationProvider().getConfiguration(), new AlwaysDevelopmentEnvironmentResolver());
        annotationConfigurer.postProcessAfterInstantiation(testClazz, "testBean");
        assertEquals(development, testClazz.getEnvironment());
        assertEquals("development", testClazz.getEnvironmentAsString());
    }

    private class TestClazz {
        @Environment
        private MyEnvironments environment;
        @Environment
        private String environmentAsString;

        public MyEnvironments getEnvironment() {
            return environment;
        }

        public String getEnvironmentAsString() {
            return environmentAsString;
        }
    }

    public enum MyEnvironments {
        development, test
    }

}
