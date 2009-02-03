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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * -DAPP_LABELS=a,b,c,d
 * 
 * @author <a href="mailto:kristoffer.moum@arktekk.no">Kristoffer Moum</a>
 */
public class DefaultConfigurationContextResolver implements ConfigurationContextResolver {

    private static final String APP_LABELS = "APP_LABELS";

    public List<String> getLabels() {
        return new ArrayList<String>() {
            {
                addAll(Arrays.asList(getFromSystemPropertyOrSystemEnv().split(",")));
            }
        };
    }

    private String getFromSystemPropertyOrSystemEnv() {
        String assemblyEnvironment = System.getProperty(APP_LABELS);
        if (null == assemblyEnvironment) {
            assemblyEnvironment = System.getenv(APP_LABELS);
        }
        return assemblyEnvironment;
    }

}
