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
package org.constretto.spring.annotation;

import java.lang.annotation.*;

/**
 * Indicated for what environment the annotated class should be a candidate for autowiring.
 * <p/>
 * When used on fields if will instruct the Spring container to inject the current environment into the annotated field.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Environment {
    public static final String DEVELOPMENT = "development";
    public static final String TEST = "test";
    public static final String PRODUCTION = "production";

    String value() default "";

    String[] tags() default {};
}
