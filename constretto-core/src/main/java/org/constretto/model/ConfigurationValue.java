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
package org.constretto.model;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationValue {
    public static final String DEFAULT_TAG = "[default-tag]";
    public static final String ALL_TAG = "[all-tag]";


    private final String tag;
    private CValue value;


    public ConfigurationValue(CValue value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    public ConfigurationValue(CValue value) {
        this.value = value;
        this.tag = DEFAULT_TAG;
    }

    public CValue value() {
        return value;
    }

    public String tag() {
        return tag;
    }


    public String toString() {
        return "ConfigurationValue{" +
                "tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
