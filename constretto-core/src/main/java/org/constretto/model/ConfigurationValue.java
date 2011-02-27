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
    private static final String ROOT_ELEMENT_NAME = "root-element";
    private final String tag;
    private String value;


    public ConfigurationValue(String value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    public ConfigurationValue(String value) {
        this.value = value;
        this.tag = DEFAULT_TAG;
    }

    public String value() {
        return value;
    }

    public String tag() {
        return tag;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationValue that = (ConfigurationValue) o;

        if (tag != null ? !tag.equals(that.tag) : that.tag != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result = tag != null ? tag.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ConfigurationValue{" +
                "tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
