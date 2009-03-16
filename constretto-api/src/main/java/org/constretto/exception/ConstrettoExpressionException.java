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
package org.constretto.exception;

import java.util.List;

/**
 * Thrown when a expression could not be found, or the expression is illegal in it self.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoExpressionException extends ConstrettoException {
    private final String expression;
    private List<String> currentTags;

    public ConstrettoExpressionException(String expression, List<String> currentTags, String message) {
        super(message);
        this.expression = expression;
        this.currentTags = currentTags;
    }

    @Override
    public String toString() {
        return "Evalutation of expression [" + expression + "] failed with message: " + getMessage() + ". Current tags " + currentTags + "";
    }

}
