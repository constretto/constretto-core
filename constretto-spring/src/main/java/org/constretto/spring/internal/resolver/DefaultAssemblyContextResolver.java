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
package org.constretto.spring.internal.resolver;

import org.constretto.spring.annotation.Environment;
import org.constretto.spring.resolver.AssemblyContextResolver;

/**
 * Provides the default assembly environment resolving strategy which is used if no other implementation found in the
 * current sprint context.
 * <p/>
 * <p/>
 * It looks for a system property called ASSEMBLY_ENV, and if that is not found tries to find the variable in the system
 * environment.
 * <p/>
 * <p/>
 * If no system property or system environment variable found, it default to Enviroment.DEVELOPMENT.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class DefaultAssemblyContextResolver implements AssemblyContextResolver {
    public static final String ASSEMBLY_KEY = "CONSTRETTO_ENV";

    public String getAssemblyContext() {
        String assemblyEnvironment = System.getProperty(ASSEMBLY_KEY);
        if (null == assemblyEnvironment) {
            assemblyEnvironment = System.getenv(ASSEMBLY_KEY);
        }
        return null != assemblyEnvironment ? assemblyEnvironment : Environment.DEVELOPMENT;
    }

    public boolean isAssemblyContextDefined() {
        return System.getProperty(ASSEMBLY_KEY) != null || System.getenv(ASSEMBLY_KEY) != null;
    }

}
