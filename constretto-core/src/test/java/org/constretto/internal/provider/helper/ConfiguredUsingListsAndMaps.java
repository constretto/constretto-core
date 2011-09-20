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
package org.constretto.internal.provider.helper;

import org.constretto.ConfigurationDefaultValueFactory;
import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfiguredUsingListsAndMaps {

    @Configuration("map")
    public Map<String, String> mapFromField;
    @Configuration("array")
    public List<String> arrayFromField;

    public Map<String, String> mapFromMethod;
    public List<String> arrayFromMethod;


    @Configure
    public void configureMe(
            @Configuration Map<String, String> map,
            @Configuration List<String> array) {
        this.arrayFromMethod = array;
        this.mapFromMethod = map;
    }
}