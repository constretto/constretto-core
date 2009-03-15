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
package org.constretto.internal.resolver;

import org.constretto.ConfigurationContextResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * -DCONSTRETTO_TAGS=a,b,c,d
 *
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class DefaultConfigurationContextResolver implements ConfigurationContextResolver {

    private static final String TAGS = "CONSTRETTO_TAGS";

    public List<String> getTags() {
        return new ArrayList<String>() {
            {
                addAll(Arrays.asList(getFromSystemPropertyOrSystemEnv().split(",")));
            }
        };
    }

    private String getFromSystemPropertyOrSystemEnv() {
        String assemblyEnvironment = System.getProperty(TAGS);
        if (null == assemblyEnvironment) {
            assemblyEnvironment = System.getenv(TAGS);
        }
        return assemblyEnvironment;
    }

}
