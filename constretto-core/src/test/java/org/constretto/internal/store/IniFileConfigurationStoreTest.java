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
 */package org.constretto.internal.store;

import org.constretto.model.PropertySet;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class IniFileConfigurationStoreTest {

    private IniFileConfigurationStore store;

    @Before
    public void setUp() {
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(new FileSystemResource("src/test/resources/test.ini"));
        store = new IniFileConfigurationStore(resources);
    }

    @Test
    public void loadIniFile() {
        List<PropertySet> props = store.load();
        assertEquals("Unexpected number of labels in ini file", 2, props.size());
        assertNotNull(props);
    }

}
