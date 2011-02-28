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

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfiguredUsingDefaults {

    @Configuration(defaultValue = "default-username")
    private String strangeUserName;

    @Configuration(defaultValue = "default-vendor")
    private String vendor;

    private String password;
    private Integer version;


    @Configure
    public void configureMe(
            @Configuration(defaultValue = "default-password") String strangePassword,
            @Configuration(defaultValueFactory = VersionDefaultValueFactory.class) Integer strangeVersion) {
        this.password = strangePassword;
        this.version = strangeVersion;
    }


    public String getStrangeUserName() {
        return strangeUserName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getVersion() {
        return version;
    }

    public String getVendor() {
        return vendor;
    }

    public static class VersionDefaultValueFactory implements ConfigurationDefaultValueFactory<Integer> {
        public Integer getDefaultValue() {
            return Integer.MIN_VALUE;
        }
    }
}