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

import org.constretto.annotation.Configure;
import org.constretto.annotation.Property;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DataSourceConfiguration {

    private String myUrl;
    private String myPassword;
    private Integer version;

    @Property
    private String vendor;

    @Property(name = "username")
    private String myUsername;


    @Configure
    public void configureMe(@Property String url, @Property(name = "password") String secret) {
        this.myUrl = url;
        this.myPassword = secret;

    }

    public String getUrl() {
        return myUrl;
    }

    public String getUsername() {
        return myUsername;
    }

    public String getPassword() {
        return myPassword;
    }

    public String getVendor() {
        return vendor;
    }

    public Integer getVersion() {
        return version;
    }

    @Configure
    public void setVersion(Integer version) {
        this.version = version;
    }
}
