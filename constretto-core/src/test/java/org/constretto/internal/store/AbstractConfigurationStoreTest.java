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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.constretto.ConfigurationStore;
import org.constretto.model.PropertySet;
import org.junit.Test;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
public abstract class AbstractConfigurationStoreTest {

    @Test
    public void load() {
        Collection<PropertySet> props = getStore().load();
        assertNotNull(props);
        assertEquals("Unexpected number of labels in ini file", 3, props.size());
        for (PropertySet prop : props) {
            String value = prop.getProperties().get("somedb.username");
            if (prop.getLabel() == null) {
                assertEquals("user0", value);
            } else if (prop.getLabel().equals("production")) {
                assertEquals("user1", value);
            } else if (prop.getLabel().equals("systest")) {
                assertEquals("user2", value);
            } else {
                fail("Unexpected label " + prop.getLabel());
            }
            assertNull(prop.getProperties().get("barekudd"));
        }
    }

    abstract protected ConfigurationStore getStore();

}
