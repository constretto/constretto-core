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
package org.constretto.spring.internal;

import org.constretto.spring.EnvironmentAnnotationConfigurer;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;

/**
 * Internal class that reads &#064;Environment annotations on classes and removes all classes that are not annotated
 * with the current environment as autowire candidates.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoAutowireCandidateResolver implements AutowireCandidateResolver {
    private QualifierAnnotationAutowireCandidateResolver annotationAutowireCandidateResolver = new QualifierAnnotationAutowireCandidateResolver();

    public ConstrettoAutowireCandidateResolver() {
    }

    @SuppressWarnings("unchecked")
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (bdHolder.getBeanDefinition().getAttribute(EnvironmentAnnotationConfigurer.INCLUDE_IN_COLLECTIONS) != null && descriptor.getCollectionType() != null) {
            Class[] interfaces = (Class[]) bdHolder.getBeanDefinition().getAttribute(EnvironmentAnnotationConfigurer.INCLUDE_IN_COLLECTIONS);
            for (Class anInterface : interfaces) {
                if (descriptor.getCollectionType().isAssignableFrom(anInterface)) {
                    return true;
                }
            }
        }
        return annotationAutowireCandidateResolver.isAutowireCandidate(bdHolder, descriptor);
    }
}
