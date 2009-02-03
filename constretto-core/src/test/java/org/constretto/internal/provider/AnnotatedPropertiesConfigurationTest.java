/*
 * Copyright 2008 the original author or authors.
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

import org.constretto.Constretto;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.store.PropertiesStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class AnnotatedPropertiesConfigurationTest {

    private ConstrettoConfiguration config;

    @Before
    public void setUp() {
        Resource annotatedProperties = new FileSystemResource("src/test/resources/annotatedTest.properties");
        config = new Constretto().addLabel("dev").addConfigurationStore(new PropertiesStore()).addResource(annotatedProperties).done().getConfiguration();
    }

    @Test
    public void getLabelledProperty() {
        Assert.assertEquals("devuser", config.evaluateToString("datasource.username"));
    }

}
