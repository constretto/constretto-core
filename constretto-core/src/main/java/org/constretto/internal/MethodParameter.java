/*
 * Copyright 2002-2010 the original author or authors.
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

package org.constretto.internal;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that encapsulates the specification of a method parameter, i.e.
 * a Method or Constructor plus a parameter index and a nested type index for
 * a declared generic type. Useful as a specification object to pass along.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andy Clement
 * @see GenericCollectionTypeResolver
 * @since 2.0
 */
public class MethodParameter {

    private Method method;

    private Constructor constructor;

    private final int parameterIndex;

    private Class<?> parameterType;

    private Annotation[] parameterAnnotations;

    private int nestingLevel = 1;

    /**
     * Map from Integer level to Integer type index
     */
    private Map<Integer, Integer> typeIndexesPerLevel;

    Map<TypeVariable, Type> typeVariableMap;


    /**
     * Create a new MethodParameter for the given method, with nesting level 1.
     *
     * @param method         the Method to specify a parameter for
     * @param parameterIndex the index of the parameter
     */
    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    /**
     * Create a new MethodParameter for the given method.
     *
     * @param method         the Method to specify a parameter for
     * @param parameterIndex the index of the parameter
     *                       (-1 for the method return type; 0 for the first method parameter,
     *                       1 for the second method parameter, etc)
     * @param nestingLevel   the nesting level of the target type
     *                       (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     *                       nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.nestingLevel = nestingLevel;
    }

    /**
     * Create a new MethodParameter for the given constructor, with nesting level 1.
     *
     * @param constructor    the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     */
    public MethodParameter(Constructor constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    /**
     * Create a new MethodParameter for the given constructor.
     *
     * @param constructor    the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param nestingLevel   the nesting level of the target type
     *                       (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     *                       nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Constructor constructor, int parameterIndex, int nestingLevel) {
        if (constructor == null) {
            throw new NullPointerException("Constructor must not be null");
        }
        this.constructor = constructor;
        this.parameterIndex = parameterIndex;
        this.nestingLevel = nestingLevel;
    }

    /**
     * Return the wrapped Method, if any.
     * <p>Note: Either Method or Constructor is available.
     *
     * @return the Method, or <code>null</code> if none
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Return the wrapped Constructor, if any.
     * <p>Note: Either Method or Constructor is available.
     *
     * @return the Constructor, or <code>null</code> if none
     */
    public Constructor getConstructor() {
        return this.constructor;
    }

    /**
     * Return the class that declares the underlying Method or Constructor.
     */
    public Class getDeclaringClass() {
        return (this.method != null ? this.method.getDeclaringClass() : this.constructor.getDeclaringClass());
    }

    /**
     * Return the index of the method/constructor parameter.
     *
     * @return the parameter index (never negative)
     */
    public int getParameterIndex() {
        return this.parameterIndex;
    }

    /**
     * Set a resolved (generic) parameter type.
     */
    void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * Return the type of the method/constructor parameter.
     *
     * @return the parameter type (never <code>null</code>)
     */
    public Class<?> getParameterType() {
        if (this.parameterType == null) {
            if (this.parameterIndex < 0) {
                this.parameterType = (this.method != null ? this.method.getReturnType() : null);
            } else {
                this.parameterType = (this.method != null ?
                        this.method.getParameterTypes()[this.parameterIndex] :
                        this.constructor.getParameterTypes()[this.parameterIndex]);
            }
        }
        return this.parameterType;
    }

    /**
     * Return the annotations associated with the target method/constructor itself.
     */
    public Annotation[] getMethodAnnotations() {
        return (this.method != null ? this.method.getAnnotations() : this.constructor.getAnnotations());
    }


    /**
     * Return the annotations associated with the specific method/constructor parameter.
     */
    public Annotation[] getParameterAnnotations() {
        if (this.parameterAnnotations == null) {
            Annotation[][] annotationArray = (this.method != null ?
                    this.method.getParameterAnnotations() : this.constructor.getParameterAnnotations());
            if (this.parameterIndex >= 0 && this.parameterIndex < annotationArray.length) {
                this.parameterAnnotations = annotationArray[this.parameterIndex];
            } else {
                this.parameterAnnotations = new Annotation[0];
            }
        }
        return this.parameterAnnotations;
    }

    /**
     * Return the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List).
     */
    public int getNestingLevel() {
        return this.nestingLevel;
    }

    /**
     * Return the type index for the specified nesting level.
     *
     * @param nestingLevel the nesting level to check
     * @return the corresponding type index, or <code>null</code>
     *         if none specified (indicating the default type index)
     */
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return getTypeIndexesPerLevel().get(nestingLevel);
    }

    /**
     * Obtain the (lazily constructed) type-indexes-per-level Map.
     */
    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<Integer, Integer>(4);
        }
        return this.typeIndexesPerLevel;
    }

}
