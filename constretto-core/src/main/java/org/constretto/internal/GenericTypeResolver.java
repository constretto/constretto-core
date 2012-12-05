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


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Helper class for resolving generic types against type variables.
 *
 * <p>Mainly intended for usage within the framework, resolving method
 * parameter types even when they are declared generically.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.5.2
 * @see GenericCollectionTypeResolver
 */
public abstract class GenericTypeResolver {

	/** Cache from Class to TypeVariable Map */
	private static final Map<Class, Reference<Map<TypeVariable, Type>>> typeVariableCache =
			Collections.synchronizedMap(new WeakHashMap<Class, Reference<Map<TypeVariable, Type>>>());


	/**
	 * Determine the target type for the given parameter specification.
	 * @param methodParam the method parameter specification
	 * @return the corresponding generic parameter type
	 */
	public static Type getTargetType(MethodParameter methodParam) {
		if (methodParam.getConstructor() != null) {
			return methodParam.getConstructor().getGenericParameterTypes()[methodParam.getParameterIndex()];
		}
		else {
			if (methodParam.getParameterIndex() >= 0) {
				return methodParam.getMethod().getGenericParameterTypes()[methodParam.getParameterIndex()];
			}
			else {
				return methodParam.getMethod().getGenericReturnType();
			}
		}
	}

	private static Class[] doResolveTypeArguments(Class ownerClass, Class classToIntrospect, Class genericIfc) {
		while (classToIntrospect != null) {
			if (genericIfc.isInterface()) {
				Type[] ifcs = classToIntrospect.getGenericInterfaces();
				for (Type ifc : ifcs) {
					Class[] result = doResolveTypeArguments(ownerClass, ifc, genericIfc);
					if (result != null) {
						return result;
					}
				}
			}
			else {
				Class[] result = doResolveTypeArguments(
						ownerClass, classToIntrospect.getGenericSuperclass(), genericIfc);
				if (result != null) {
					return result;
				}
			}
			classToIntrospect = classToIntrospect.getSuperclass();
		}
		return null;
	}

    @SuppressWarnings("unchecked")
	private static Class[] doResolveTypeArguments(Class ownerClass, Type ifc, Class genericIfc) {
		if (ifc instanceof ParameterizedType) {
			ParameterizedType paramIfc = (ParameterizedType) ifc;
			Type rawType = paramIfc.getRawType();
			if (genericIfc.equals(rawType)) {
				Type[] typeArgs = paramIfc.getActualTypeArguments();
				Class[] result = new Class[typeArgs.length];
				for (int i = 0; i < typeArgs.length; i++) {
					Type arg = typeArgs[i];
					result[i] = extractClass(ownerClass, arg);
				}
				return result;
			}
			else if (genericIfc.isAssignableFrom((Class) rawType)) {
				return doResolveTypeArguments(ownerClass, (Class) rawType, genericIfc);
			}
		}
		else if (genericIfc.isAssignableFrom((Class) ifc)) {
			return doResolveTypeArguments(ownerClass, (Class) ifc, genericIfc);
		}
		return null;
	}

	/**
	 * Extract a class instance from given Type.
	 */
	private static Class extractClass(Class ownerClass, Type arg) {
		if (arg instanceof ParameterizedType) {
			return extractClass(ownerClass, ((ParameterizedType) arg).getRawType());
		}
		else if (arg instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) arg;
			Type gt = gat.getGenericComponentType();
			Class<?> componentClass = extractClass(ownerClass, gt);
			return Array.newInstance(componentClass, 0).getClass();
		}
		else if (arg instanceof TypeVariable) {
			TypeVariable tv = (TypeVariable) arg;
			arg = getTypeVariableMap(ownerClass).get(tv);
			if (arg == null) {
				arg = extractBoundForTypeVariable(tv);
			}
			else {
				arg = extractClass(ownerClass, arg);
			}
		}
		return (arg instanceof Class ? (Class) arg : Object.class);
	}

	/**
	 * Build a mapping of {@link java.lang.reflect.TypeVariable#getName TypeVariable names} to concrete
	 * {@link Class} for the specified {@link Class}. Searches all super types,
	 * enclosing types and interfaces.
	 */
	static Map<TypeVariable, Type> getTypeVariableMap(Class clazz) {
		Reference<Map<TypeVariable, Type>> ref = typeVariableCache.get(clazz);
		Map<TypeVariable, Type> typeVariableMap = (ref != null ? ref.get() : null);

		if (typeVariableMap == null) {
			typeVariableMap = new HashMap<TypeVariable, Type>();

			// interfaces
			extractTypeVariablesFromGenericInterfaces(clazz.getGenericInterfaces(), typeVariableMap);

			// super class
			Type genericType = clazz.getGenericSuperclass();
			Class type = clazz.getSuperclass();
			while (type != null && !Object.class.equals(type)) {
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericType;
					populateTypeMapFromParameterizedType(pt, typeVariableMap);
				}
				extractTypeVariablesFromGenericInterfaces(type.getGenericInterfaces(), typeVariableMap);
				genericType = type.getGenericSuperclass();
				type = type.getSuperclass();
			}

			// enclosing class
			type = clazz;
			while (type.isMemberClass()) {
				genericType = type.getGenericSuperclass();
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericType;
					populateTypeMapFromParameterizedType(pt, typeVariableMap);
				}
				type = type.getEnclosingClass();
			}

			typeVariableCache.put(clazz, new WeakReference<Map<TypeVariable, Type>>(typeVariableMap));
		}

		return typeVariableMap;
	}

	/**
	 * Extracts the bound <code>Type</code> for a given {@link java.lang.reflect.TypeVariable}.
	 */
	static Type extractBoundForTypeVariable(TypeVariable typeVariable) {
		Type[] bounds = typeVariable.getBounds();
		if (bounds.length == 0) {
			return Object.class;
		}
		Type bound = bounds[0];
		if (bound instanceof TypeVariable) {
			bound = extractBoundForTypeVariable((TypeVariable) bound);
		}
		return bound;
	}

	private static void extractTypeVariablesFromGenericInterfaces(Type[] genericInterfaces, Map<TypeVariable, Type> typeVariableMap) {
		for (Type genericInterface : genericInterfaces) {
			if (genericInterface instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericInterface;
				populateTypeMapFromParameterizedType(pt, typeVariableMap);
				if (pt.getRawType() instanceof Class) {
					extractTypeVariablesFromGenericInterfaces(
							((Class) pt.getRawType()).getGenericInterfaces(), typeVariableMap);
				}
			}
			else if (genericInterface instanceof Class) {
				extractTypeVariablesFromGenericInterfaces(
						((Class) genericInterface).getGenericInterfaces(), typeVariableMap);
			}
		}
	}

	/**
	 * Read the {@link java.lang.reflect.TypeVariable TypeVariables} from the supplied {@link java.lang.reflect.ParameterizedType}
	 * and add mappings corresponding to the {@link java.lang.reflect.TypeVariable#getName TypeVariable name} ->
	 * concrete type to the supplied {@link java.util.Map}.
	 * <p>Consider this case:
	 * <pre class="code>
	 * public interface Foo<S, T> {
	 *  ..
	 * }
	 *
	 * public class FooImpl implements Foo<String, Integer> {
	 *  ..
	 * }</pre>
	 * For '<code>FooImpl</code>' the following mappings would be added to the {@link java.util.Map}:
	 * {S=java.lang.String, T=java.lang.Integer}.
	 */
	private static void populateTypeMapFromParameterizedType(ParameterizedType type, Map<TypeVariable, Type> typeVariableMap) {
		if (type.getRawType() instanceof Class) {
			Type[] actualTypeArguments = type.getActualTypeArguments();
			TypeVariable[] typeVariables = ((Class) type.getRawType()).getTypeParameters();
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type actualTypeArgument = actualTypeArguments[i];
				TypeVariable variable = typeVariables[i];
				if (actualTypeArgument instanceof Class) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof GenericArrayType) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof ParameterizedType) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof TypeVariable) {
					// We have a type that is parameterized at instantiation time
					// the nearest match on the bridge method will be the bounded type.
					TypeVariable typeVariableArgument = (TypeVariable) actualTypeArgument;
					Type resolvedType = typeVariableMap.get(typeVariableArgument);
					if (resolvedType == null) {
						resolvedType = extractBoundForTypeVariable(typeVariableArgument);
					}
					typeVariableMap.put(variable, resolvedType);
				}
			}
		}
	}

}
