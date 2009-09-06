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
package org.constretto.spring.resolver;

import java.util.List;

/**
 * The interface used to resolve the environment the application is running in.
 * <p/>
 * <p/>
 * To customize the default behavour, create an implementation of this interface and register your implementation as a
 * bean in your spring context. Any implementation registered will be picked up and used instead of the default. If two
 * or more implementations found an error is raised.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public interface AssemblyContextResolver {
    List<String> getAssemblyContext();

}
