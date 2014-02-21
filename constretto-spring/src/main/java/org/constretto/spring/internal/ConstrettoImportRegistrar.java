package org.constretto.spring.internal;

import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoException;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.ConstrettoPropertyPlaceholderConfigurer;
import org.constretto.spring.annotation.Constretto;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for registering Constretto Spring's BeanPostProcessors when using Spring JavaConfig.
 *
 * @author zapodot at gmail dot com
 */
public class ConstrettoImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata,
                                        final BeanDefinitionRegistry registry) {
        final ConstrettoConfiguration constrettoConfiguration = getConstrettoConfigurationForConfigurationClass(importingClassMetadata.getClassName());
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(Constretto.class.getName()));
        if (annotationAttributes.getBoolean("enablePropertyPlaceholder")) {
            registry.registerBeanDefinition(ConstrettoPropertyPlaceholderConfigurer.class.getName(), createPropertyPlaceholderBeanDefinition(constrettoConfiguration));
        }
        if (annotationAttributes.getBoolean("enableAnnotationSupport")) {
            registry.registerBeanDefinition(ConfigurationAnnotationConfigurer.class.getName(), createConfigurationAnnotationConfigurerBeanDefinition(constrettoConfiguration));
        }


    }

    private BeanDefinition createConfigurationAnnotationConfigurerBeanDefinition(final ConstrettoConfiguration constrettoConfiguration) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ConfigurationAnnotationConfigurer.class);
        final ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, constrettoConfiguration);
        argumentValues.addIndexedArgumentValue(1, new DefaultAssemblyContextResolver());
        beanDefinition.setConstructorArgumentValues(argumentValues);
        return beanDefinition;
    }

    private BeanDefinition createPropertyPlaceholderBeanDefinition(final ConstrettoConfiguration constrettoConfiguration) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ConstrettoPropertyPlaceholderConfigurer.class);
        final ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, constrettoConfiguration);
        beanDefinition.setConstructorArgumentValues(argumentValues);
        return beanDefinition;
    }

    private ConstrettoConfiguration getConstrettoConfigurationForConfigurationClass(String className) {

        Class<?> configurationClass;
        try {
            configurationClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ConstrettoException(String.format("Could not load configuration class \"%s\"", className), e);
        }
        final List<Method> methods = findStaticNonArgsMethodsReturningConstrettoConfiguration(configurationClass);
        if (methods.isEmpty()) {
            throw new ConstrettoException("Could not find a static factory non-arg method that creates a " +
                    "org.constretto.ConstrettoConfiguration instance in your configuration class (or superclass). " +
                    "In order to use automatic setup of Constretto-Spring BeanPostProcessors " +
                    "you will have to define one");
        } else if (methods.size() > 1) {
            throw new ConstrettoException("Found more than static non-arg method returning a " +
                    "org.constretto.ConstrettoConfiguration instance in your configuration class (or superclass). " +
                    "To use automatic setup of Constretto Spring BeanPostProcessors you should have only one");
        } else {
            Method factoryMethod = methods.get(0);
            try {
                return (ConstrettoConfiguration) factoryMethod.invoke(null);
            } catch (IllegalAccessException e) {
                throw new ConstrettoException(String.format("Could not invoke factory method \"%1$s\" in configuration class \"%2$\"", factoryMethod.getName(), configurationClass.getName()), e);
            } catch (InvocationTargetException e) {
                throw new ConstrettoException(String.format("Could not invoke factory method \"%1$s\" in configuration class \"%2$\"", factoryMethod.getName(), configurationClass.getName()), e);
            }
        }
    }

    private List<Method> findStaticNonArgsMethodsReturningConstrettoConfiguration(Class<?> configurationClass) {
        return filterMethodsHavingArgs(
                filterMethodsReturningByReturnTypeNotBeingConstretto(
                        findPublicStaticMethods(configurationClass)));
    }

    private List<Method> filterMethodsHavingArgs(Iterable<Method> methods) {
        List<Method> nonArgsMethods = new LinkedList<Method>();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0) {
                nonArgsMethods.add(method);
            }
        }
        return nonArgsMethods;
    }

    private List<Method> filterMethodsReturningByReturnTypeNotBeingConstretto(Iterable<Method> methods) {
        List<Method> constrettoMethods = new LinkedList<Method>();
        for (Method method : methods) {
            if (ConstrettoConfiguration.class.isAssignableFrom(method.getReturnType())) {
                constrettoMethods.add(method);
            }
        }
        return constrettoMethods;
    }

    private List<Method> findPublicStaticMethods(Class<?> configurationClass) {
        List<Method> staticMethods = new LinkedList<Method>();
        for (Method method : configurationClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                staticMethods.add(method);
            }
        }
        return staticMethods;
    }
}
