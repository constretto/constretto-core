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
package org.constretto.annotation;

import org.constretto.ConfigurationDefaultValueFactory;

import java.lang.annotation.*;

/**
 * This annotation is picked up by Constretto, and applies to fields that are declared as private, public or default,
 * including those inherited from superclasses.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Configuration {

    /**
     * The expression of the property to lookup in constretto.
     * <p/>
     * It is important that environment prefixes is not used in this attribute
     */
    String expression() default "";

    /**
     * A description of the usage of the property. Not directly used in the runtime environment, but may be
     * useful for documentation or monitoring situations
     */
    String description() default "";

    /**
     * States the default value to be injected if no value found associated for the expression specified in the
     * expression attribute.
     * <p/>
     * When a default value is set, the required attribute will be ignored.
     */
    String defaultValue() default "N/A";

    /**
     * Use when more complex default values needs to be injected of no value found associated with the expression
     * specified in the expression attribute
     */
    Class<? extends ConfigurationDefaultValueFactory<?>> defaultValueFactory() default EmptyValueFactory.class;

    /**
     * Declares whether it is required to find the specified property key.
     * <p/>
     * Defaults to <code>true</code>.
     */
    boolean required() default true;

    //
    // helper
    //
    public static class EmptyValueFactory implements ConfigurationDefaultValueFactory<Object> {
        public Object getDefaultValue() {
            return null;
        }
    }
}