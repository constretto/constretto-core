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
package org.constretto.internal.model;

import static junit.framework.Assert.assertEquals;
import org.constretto.model.ConfigurationNode;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationNodeTest {

    @Test
    public void setupAndSearchModel() {
        ConfigurationNode configurationNode = ConfigurationNode.createRootElement();
        configurationNode.update("key-1", "value-1", ConfigurationNode.DEFAULT_TAG);
        configurationNode.update("key-1", "value-2", "tag-1");
        configurationNode.update("key-1", "value-3", "tag-2");
        configurationNode.update("parent-1.key-2", "value-4", ConfigurationNode.DEFAULT_TAG);
        configurationNode.update("parent-1.key-2", "value-5", "tag-1");
        configurationNode.update("parent-1.key-2", "value-6", "tag-2");
        configurationNode.update("parent-2.child-1.key-3", "value-7", ConfigurationNode.DEFAULT_TAG);
        configurationNode.update("parent-2.child-1.key-3", "value-8", "tag-1");
        configurationNode.update("parent-2.child-1.key-3", "value-9", "tag-2");
        configurationNode.update("parent-3.child-2.child3.key-4", "value-10", ConfigurationNode.DEFAULT_TAG);


        List<ConfigurationNode> key1Node = configurationNode.findAllBy("key-1");
        assertEquals(3, key1Node.size());

        List<ConfigurationNode> key2Node = configurationNode.findAllBy("parent-1.key-2");
        assertEquals(3, key2Node.size());

        List<ConfigurationNode> key3Node = configurationNode.findAllBy("parent-2.child-1.key-3");
        assertEquals(3, key3Node.size());

        List<ConfigurationNode> key4Node = configurationNode.findAllBy("parent-3.child-2.child3.key-4");
        assertEquals(1, key4Node.size());
        assertEquals("parent-3.child-2.child3.key-4", key4Node.get(0).getExpression());


    }


}
