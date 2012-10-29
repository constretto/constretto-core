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
package org.constretto.model;

import java.io.InputStream;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ClassPathResource extends Resource {
    public ClassPathResource(String path) {
        super(path);
    }

    @Override
    public InputStream getInputStream() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String location;
        if (path.startsWith(CLASSPATH_PREFIX)) {
            location = path.substring(CLASSPATH_PREFIX.length(), path.length());
        } else {
            location = path;
        }
        return classLoader.getResourceAsStream(location);
    }

    @Override
    public boolean exists() {
        InputStream is = getInputStream();
        boolean result = is != null;
        try{
            is.close();
        } catch (Exception e) {
        }
        return result;
    }
}
