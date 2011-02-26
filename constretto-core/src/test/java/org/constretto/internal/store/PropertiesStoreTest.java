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
package org.constretto.internal.store;

import org.constretto.ConfigurationStore;
import org.constretto.model.ClassPathResource;
import org.constretto.model.Resource;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
public class PropertiesStoreTest extends AbstractConfigurationStoreTest {

    @Override
    protected ConfigurationStore getStore() {
        Resource props = new ClassPathResource("test.properties");
        return new PropertiesStore().addResource(props);
    }

}
