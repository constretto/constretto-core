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

import org.constretto.annotation.Tags;
import org.constretto.spring.annotation.Environment;
import org.constretto.test.helper.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
@Tags("springjunit")
@Environment("springjunit")
@RunWith(ConstrettoSpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConstrettoSpringJUnit4ClassRunnerTest {

    @Autowired
    TestBean testBean;

    @Test
    public void givenEnvironmentAnnotationOnTestClassWhenRunningTestThenConstrettoKnowsEnvironment() {
        List<String> expected = new ArrayList<String>() {{
            add("springjunit");
        }};
        Assert.assertArrayEquals(expected.toArray(new String[0]), testBean.currentEnvironment.toArray(new String[0]));
        Assert.assertEquals("green", testBean.color.name());
    }

    static class TestBean {

        @Autowired
        Color color;

        @Tags
        List<String> currentEnvironment;
    }

}
