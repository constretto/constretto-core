/*
 * Copyright 2011 the original author or authors.
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

import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ContagiousConfigurationMethod {

    private String missingValue;
    private String otherMissingValue;
    private String valueWithDefault;

    @Configure
    public void configure(@Configuration(value = "missing-value", required = false) String missingValue,
                          @Configuration(value = "i-have-default", defaultValue = "default-value") String valueWithDefault,
                          @Configuration(value = "other-missing-value", required = false) String otherMissingValue) {
        this.missingValue = missingValue;
        this.valueWithDefault = valueWithDefault;
        this.otherMissingValue = otherMissingValue;
    }

    public String missingValue() {
        return missingValue;
    }

    public String otherMissingValue() {
        return otherMissingValue;
    }

    public String valueWithDefault() {
        return valueWithDefault;
    }
}
