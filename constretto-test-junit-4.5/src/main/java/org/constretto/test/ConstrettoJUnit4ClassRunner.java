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
import org.constretto.internal.ConstrettoUtils;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
public class ConstrettoJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    public ConstrettoJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    private String changeTagsSystemProperty() {
        Tags tags = getTestClass().getJavaClass().getAnnotation(Tags.class);
        if (tags != null) {
            String tagcsv = ConstrettoUtils.asCsv(tags.value());
            return System.setProperty(DefaultConfigurationContextResolver.TAGS, tagcsv.toString());
        }
        return System.getProperty(DefaultConfigurationContextResolver.TAGS);
    }

    @Override
    public void run(RunNotifier notifier) {
        String originalValue = changeTagsSystemProperty();
        super.run(notifier);
        if (originalValue == null) {
            System.getProperties().remove(DefaultConfigurationContextResolver.TAGS);
        } else {
            System.setProperty(DefaultConfigurationContextResolver.TAGS, originalValue);
        }
    }

}
