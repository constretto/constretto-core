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
package org.constretto.spring.configuration.helper;

import java.io.File;

import org.constretto.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ValidBeanUsingPropertyEditors {
    @Configuration(expression = "long")
    private Long longProperty;
    @Configuration(expression = "resource")
    private Resource resourceProperty;
    @Configuration(expression = "file")
    private File fileProperty;

    public Long getLongProperty() {
        return longProperty;
    }

    public Resource getResourceProperty() {
        return resourceProperty;
    }

    public File getFileProperty() {
        return fileProperty;
    }

}