/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.constretto.test;

import org.constretto.annotation.Tags;
import org.constretto.test.extractors.ConstrettoEnvironmentExtractor;
import org.constretto.test.extractors.ConstrettoTagExtractor;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.constretto.internal.ConstrettoUtils.asCsv;
import static org.constretto.internal.resolver.DefaultConfigurationContextResolver.TAGS;
import static org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;

/**
 * Sets the <code>CONSTRETTO_TAGS</code> and <code>CONSTRETTO_ENV</code> system properties corresponding to
 * the value of the annotations {@link Tags} and {@link org.constretto.spring.annotation.Environment}, respectively, on the test class.
 *
 * @author <a href="mailto:from.github@nisgits.net">Stig Kleppe-Jorgensen</a>, 2013.01.14
 */
public class ConstrettoRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                final String originalTags = changeTagsSystemProperty(description);
                final String originalEnvironment = changeEnvironmentSystemProperty(description);

                try {
                    base.evaluate();
                } finally {
                    if (originalTags == null) {
                        System.getProperties().remove(TAGS);
                    } else {
                        System.setProperty(TAGS, originalTags);
                    }

                    if (originalEnvironment == null) {
                        System.getProperties().remove(ASSEMBLY_KEY);
                    } else {
                        System.setProperty(ASSEMBLY_KEY, originalEnvironment);
                    }
                }
            }
        };
    }


    private String changeTagsSystemProperty(Description description) {
        final String[] tagValue = ConstrettoTagExtractor.findTagValueForDescription(description);

        if (tagValue == null) {
            return System.getProperty(TAGS);
        } else {
            return System.setProperty(TAGS, asCsv(tagValue));
        }
    }

    private String changeEnvironmentSystemProperty(Description description) {

        if(constrettoEnvironmentAnnotationIsOnClasspath()) {
            final String[] environmentsDeclared = ConstrettoEnvironmentExtractor.extractEnvironmentValue(description);
            if (environmentsDeclared != null) {
                return System.setProperty(ASSEMBLY_KEY, asCsv(environmentsDeclared));
            }
        }
        return System.getProperty(ASSEMBLY_KEY);

    }

    private boolean constrettoEnvironmentAnnotationIsOnClasspath() {
        try {
            Class.forName("org.constretto.spring.annotation.Environment");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
