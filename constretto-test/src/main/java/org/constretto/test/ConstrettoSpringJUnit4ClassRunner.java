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
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor &Aring;ge Eldby</a>
 */
public class ConstrettoSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    public ConstrettoSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        String originalTags = changeTagsSystemProperty();
        String originalEnvironment = changeAssembleSystemProperty();
        super.run(notifier);
        if (originalTags == null) {
            System.getProperties().remove(DefaultConfigurationContextResolver.TAGS);
        } else {
            System.setProperty(DefaultConfigurationContextResolver.TAGS, originalTags);
        }
        if (originalEnvironment == null) {
            System.getProperties().remove(DefaultAssemblyContextResolver.ASSEMBLY_KEY);
        } else {
            System.setProperty(DefaultAssemblyContextResolver.ASSEMBLY_KEY, originalEnvironment);
        }
    }

    private String changeTagsSystemProperty() {
        Tags tags = getTestClass().getJavaClass().getAnnotation(Tags.class);
        if (tags != null) {
            return System.setProperty(DefaultConfigurationContextResolver.TAGS, ConstrettoUtils.asCsv(tags.value()));
        }
        return System.getProperty(DefaultConfigurationContextResolver.TAGS);
    }

    private String changeAssembleSystemProperty() {
        Environment environment = getTestClass().getJavaClass().getAnnotation(Environment.class);
        if (environment != null) {
            return System.setProperty(DefaultAssemblyContextResolver.ASSEMBLY_KEY, ConstrettoUtils.asCsv(environment.value()));
        }
        return System.getProperty(DefaultAssemblyContextResolver.ASSEMBLY_KEY);
    }


}
