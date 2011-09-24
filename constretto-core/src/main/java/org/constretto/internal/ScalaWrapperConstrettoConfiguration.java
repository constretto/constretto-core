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
package org.constretto.internal;


import org.constretto.model.ConfigurationValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ScalaWrapperConstrettoConfiguration extends DefaultConstrettoConfiguration {

    public ScalaWrapperConstrettoConfiguration(Map<String, List<ConfigurationValue>> configuration, List<String> originalTags) {
        super(configuration, originalTags);
    }

    public ScalaWrapperConstrettoConfiguration(Map<String, List<ConfigurationValue>> configuration) {
        super(configuration);
    }

    @Override
    protected String processVariablesInProperty(final String expression, final Collection<String> visitedPlaceholders) {
        visitedPlaceholders.add(expression);
        ConfigurationValue currentNode = findElementOrThrowException(expression);

        String value = currentNode.value();
        if (valueNeedsVariableResolving(value)) {
            value = substituteVariablesinValue(value, visitedPlaceholders);
        }
        return value;
    }

    @Override
    protected String substituteVariablesinValue(String value, final Collection<String> visitedPlaceholders) {
        while (valueNeedsVariableResolving(value)) {
            ConfigurationVariable expresionToLookup = extractConfigurationVariable(value);
            ScalaWrapperConstrettoConfiguration rootConfig = new ScalaWrapperConstrettoConfiguration(configuration, currentTags);
            String v = rootConfig.processVariablesInProperty(expresionToLookup.expression, visitedPlaceholders);
            int start = 0;
            int end = v.length();
            if (v.startsWith("\"")) start = start + 1;
            if (v.endsWith("\"")) end = end - 1;

            value = value.substring(0, expresionToLookup.startIndex)
                    + v.substring(start, end)
                    + value.subSequence(expresionToLookup.endIndex + 1, value.length());
        }
        return value;
    }

}
