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

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for determining element types of collections and maps.
 * <p/>
 * <p>Mainly intended for usage within the framework, determining the
 * target type of values to be added to a collection or map
 * (to be able to attempt type conversion if appropriate).
 *
 * @author Juergen Hoeller
 */
public abstract class GenericCollectionTypeResolver {


    /**
     * Determine the generic element type of the given Collection field.
     *
     * @param collectionField the collection field to introspect
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getCollectionFieldType(Field collectionField) {
        return getGenericFieldType(collectionField, Collection.class, 0, getNestingLevel(collectionField.getGenericType(), 0));
    }

    /**
     * Determine the generic key type of the given Map field.
     *
     * @param mapField the map field to introspect
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getMapKeyFieldType(Field mapField) {
        return getGenericFieldType(mapField, Map.class, 0, getNestingLevel(mapField.getGenericType(), 0));
    }

    /**
     * Determine the generic value type of the given Map field.
     *
     * @param mapField the map field to introspect
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getMapValueFieldType(Field mapField) {
        return getGenericFieldType(mapField, Map.class, 1, getNestingLevel(mapField.getGenericType(), 0));
    }

    /**
     * Determine the generic element type of the given Collection parameter.
     *
     * @param methodParam the method parameter specification
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Collection.class, 0);
    }

    /**
     * Determine the generic key type of the given Map parameter.
     *
     * @param methodParam the method parameter specification
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getMapKeyParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Map.class, 0);
    }

    /**
     * Determine the generic value type of the given Map parameter.
     *
     * @param methodParam the method parameter specification
     * @return the generic type, or <code>null</code> if none
     */
    public static Class<?> getMapValueParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Map.class, 1);
    }


    /**
     * Extract the generic parameter type from the given method or constructor.
     *
     * @param methodParam the method parameter specification
     * @param source      the source class/interface defining the generic parameter types
     * @param typeIndex   the index of the type (e.g. 0 for Collections,
     *                    0 for Map keys, 1 for Map values)
     * @return the generic type, or <code>null</code> if none
     */
    private static Class<?> getGenericParameterType(MethodParameter methodParam, Class<?> source, int typeIndex) {
        return extractType(methodParam, GenericTypeResolver.getTargetType(methodParam),
                source, typeIndex, methodParam.getNestingLevel(), 1);
    }

    /**
     * Extract the generic type from the given field.
     *
     * @param field        the field to check the type for
     * @param source       the source class/interface defining the generic parameter types
     * @param typeIndex    the index of the type (e.g. 0 for Collections,
     *                     0 for Map keys, 1 for Map values)
     * @param nestingLevel the nesting level of the target type
     * @return the generic type, or <code>null</code> if none
     */
    private static Class<?> getGenericFieldType(Field field, Class<?> source, int typeIndex, int nestingLevel) {
        return extractType(null, field.getGenericType(), source, typeIndex, nestingLevel, 1);
    }

    /**
     * Recursively find the nesting level of a generic type.
     *
     * @param type 			the
     * @param nestingLevel 	calculated nesting level. Initial call using 0
     * @return
     */
    static int getNestingLevel(final Type type, final int nestingLevel){
        if(type instanceof ParameterizedType){
            final Type[] paramTypes = ((ParameterizedType) type).getActualTypeArguments();
            return getNestingLevel(paramTypes[paramTypes.length-1], nestingLevel +1);
        } else {
            return nestingLevel;
        }

    }


    /**
     * Extract the generic type from the given Type object.
     *
     * @param methodParam  the method parameter specification
     * @param type         the Type to check
     * @param source       the source collection/map Class that we check
     * @param typeIndex    the index of the actual type argument
     * @param nestingLevel the nesting level of the target type
     * @param currentLevel the current nested level
     * @return the generic type as Class, or <code>null</code> if none
     */
    private static Class<?> extractType(
            MethodParameter methodParam, Type type, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        Type resolvedType = type;
        if (type instanceof TypeVariable && methodParam != null && methodParam.typeVariableMap != null) {
            Type mappedType = methodParam.typeVariableMap.get((TypeVariable) type);
            if (mappedType != null) {
                resolvedType = mappedType;
            }
        }
        if (resolvedType instanceof ParameterizedType) {
            return extractTypeFromParameterizedType(
                    methodParam, (ParameterizedType) resolvedType, source, typeIndex, nestingLevel, currentLevel);
        } else if (resolvedType instanceof Class) {
            return extractTypeFromClass(methodParam, (Class) resolvedType, source, typeIndex, nestingLevel, currentLevel);
        } else {
            return null;
        }
    }

    /**
     * Extract the generic type from the given ParameterizedType object.
     *
     * @param methodParam  the method parameter specification
     * @param ptype        the ParameterizedType to check
     * @param source       the expected raw source type (can be <code>null</code>)
     * @param typeIndex    the index of the actual type argument
     * @param nestingLevel the nesting level of the target type
     * @param currentLevel the current nested level
     * @return the generic type as Class, or <code>null</code> if none
     */
    private static Class<?> extractTypeFromParameterizedType(MethodParameter methodParam,
                                                             ParameterizedType ptype, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        if (!(ptype.getRawType() instanceof Class)) {
            return null;
        }
        Class rawType = (Class) ptype.getRawType();
        Type[] paramTypes = ptype.getActualTypeArguments();
        if (nestingLevel - currentLevel > 0) {
            int nextLevel = currentLevel + 1;
            Integer currentTypeIndex = (methodParam != null ? methodParam.getTypeIndexForLevel(nextLevel) : null);
            // Default is last parameter type: Collection element or Map value.
            int indexToUse = (currentTypeIndex != null ? currentTypeIndex : paramTypes.length - 1);
            Type paramType = paramTypes[indexToUse];
            return extractType(methodParam, paramType, null, typeIndex, nestingLevel, nextLevel);
        }
        if (source != null && !source.isAssignableFrom(rawType)) {
            return null;
        }
        Class fromSuperclassOrInterface =
                extractTypeFromClass(methodParam, rawType, source, typeIndex, nestingLevel, currentLevel);
        if (fromSuperclassOrInterface != null) {
            return fromSuperclassOrInterface;
        }
        if (paramTypes == null || typeIndex >= paramTypes.length) {
            return null;
        }
        Type paramType = paramTypes[typeIndex];
        if (paramType instanceof TypeVariable && methodParam != null && methodParam.typeVariableMap != null) {
            Type mappedType = methodParam.typeVariableMap.get((TypeVariable) paramType);
            if (mappedType != null) {
                paramType = mappedType;
            }
        }
        if (paramType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) paramType;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds != null && upperBounds.length > 0 && !Object.class.equals(upperBounds[0])) {
                paramType = upperBounds[0];
            } else {
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds != null && lowerBounds.length > 0 && !Object.class.equals(lowerBounds[0])) {
                    paramType = lowerBounds[0];
                }
            }
        }
        if (paramType instanceof ParameterizedType) {
            paramType = ((ParameterizedType) paramType).getRawType();
        }
        if (paramType instanceof GenericArrayType) {
            // A generic array type... Let's turn it into a straight array type if possible.
            Type compType = ((GenericArrayType) paramType).getGenericComponentType();
            if (compType instanceof Class) {
                return Array.newInstance((Class) compType, 0).getClass();
            }
        } else if (paramType instanceof Class) {
            // We finally got a straight Class...
            return (Class) paramType;
        }
        return null;
    }

    /**
     * Extract the generic type from the given Class object.
     *
     * @param methodParam  the method parameter specification
     * @param clazz        the Class to check
     * @param source       the expected raw source type (can be <code>null</code>)
     * @param typeIndex    the index of the actual type argument
     * @param nestingLevel the nesting level of the target type
     * @param currentLevel the current nested level
     * @return the generic type as Class, or <code>null</code> if none
     */
    private static Class<?> extractTypeFromClass(
            MethodParameter methodParam, Class<?> clazz, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        if (clazz.getName().startsWith("java.util.")) {
            return null;
        }
        if (clazz.getSuperclass() != null && isIntrospectionCandidate(clazz.getSuperclass())) {
            return extractType(methodParam, clazz.getGenericSuperclass(), source, typeIndex, nestingLevel, currentLevel);
        }
        Type[] ifcs = clazz.getGenericInterfaces();
        if (ifcs != null) {
            for (Type ifc : ifcs) {
                Type rawType = ifc;
                if (ifc instanceof ParameterizedType) {
                    rawType = ((ParameterizedType) ifc).getRawType();
                }
                if (rawType instanceof Class && isIntrospectionCandidate((Class) rawType)) {
                    return extractType(methodParam, ifc, source, typeIndex, nestingLevel, currentLevel);
                }
            }
        }
        return null;
    }

    /**
     * Determine whether the given class is a potential candidate
     * that defines generic collection or map types.
     *
     * @param clazz the class to check
     * @return whether the given class is assignable to Collection or Map
     */
    private static boolean isIntrospectionCandidate(Class clazz) {
        return (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz));
    }

}
