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
package org.constretto.spring.internal;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.ConstrettoUtils;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.constretto.resolver.ConfigurationContextResolver;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.ConstrettoConfigurationFactoryBean;
import org.constretto.spring.ConstrettoPropertyPlaceholderConfigurer;
import org.constretto.spring.EnvironmentAnnotationConfigurer;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.constretto.spring.resolver.AssemblyContextResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoNamespaceHandler extends NamespaceHandlerSupport {
    private static final String CONFIGURATION_CONTEXT_RESOLVER_NAME = "constretto:configurationContextResolver";
    private static final String ENVIRONMENT_CONTEXT_RESOLVER_NAME = "constretto:assemblyContextResolver";
    private static final String CONSTRETTO_CONFIGURATION_BEAN_NAME = "constretto:constrettoConfiguration";
    private static final String CONSTRETTO_PLACEHOLDER_BEAN_NAME = "constretto:constrettoPlaceholderConfigurer";
    private static final String CONSTRETTO_CONFIGURATION_ANNOTATION_BEAN_NAME = "constretto:configurationAnnotationConfigurer";
    private static final String CONSTRETTO_ENVIRONMENT_ANNOTATION_BEAN_NAME = "constretto:environmentAnnotationConfigurer";

    public void init() {
        registerBeanDefinitionParser("configuration", new ConfigurationDefinitionParser());
        registerBeanDefinitionParser("import", new ImportDefinitionParser());
        registerBeanDefinitionParser("property-placeholder-configurer", new PropertyPlaceHolderDefinitionParser());
        registerBeanDefinitionParser("annotation-config", new AnnotationConfigDefinitionParser());
    }

    private void createPropertyPlaceholder(ConstrettoConfiguration configuration, ParserContext parserContext, Element propertyPlaceholderElement) {
        BeanDefinitionBuilder placeHolderBean = BeanDefinitionBuilder.rootBeanDefinition(ConstrettoPropertyPlaceholderConfigurer.class);
        if (configuration != null) {
            placeHolderBean.addConstructorArgValue(configuration);
        } else {
            placeHolderBean.addConstructorArgReference(CONSTRETTO_CONFIGURATION_BEAN_NAME);
        }

        if (propertyPlaceholderElement != null) {
            boolean ignoreUnresolved = propertyPlaceholderElement.getAttribute("ignore-unresolved-placeholders") != null ? Boolean.valueOf(propertyPlaceholderElement.getAttribute("ignore-unresolved-placeholders")) : false;
            String prefix = propertyPlaceholderElement.getAttribute("prefix") != null ? propertyPlaceholderElement.getAttribute("prefix") : "${";
            String suffix = propertyPlaceholderElement.getAttribute("suffix") != null ? propertyPlaceholderElement.getAttribute("suffix") : "}";
            placeHolderBean.addPropertyValue("placeholderPrefix", prefix);
            placeHolderBean.addPropertyValue("placeholderSuffix", suffix);
            placeHolderBean.addPropertyValue("ignoreUnresolvablePlaceholders", ignoreUnresolved);

        }
        parserContext.getRegistry().registerBeanDefinition(CONSTRETTO_PLACEHOLDER_BEAN_NAME, placeHolderBean.getBeanDefinition());
    }

    private void createAnnotationConfigBean(ConstrettoConfiguration configuration, AssemblyContextResolver assemblyContextResolver, ParserContext parserContext) {
        BeanDefinitionBuilder configurationAnnotationConfigurerBean = BeanDefinitionBuilder.rootBeanDefinition(ConfigurationAnnotationConfigurer.class);
        if (configuration != null) {
            configurationAnnotationConfigurerBean.addConstructorArgValue(configuration);
        } else {
            configurationAnnotationConfigurerBean.addConstructorArgReference(CONSTRETTO_CONFIGURATION_BEAN_NAME);
        }
        if (assemblyContextResolver != null) {
            configurationAnnotationConfigurerBean.addConstructorArgValue(assemblyContextResolver);
        } else {
            configurationAnnotationConfigurerBean.addConstructorArgReference(ENVIRONMENT_CONTEXT_RESOLVER_NAME);
        }
        parserContext.getRegistry().registerBeanDefinition(CONSTRETTO_CONFIGURATION_ANNOTATION_BEAN_NAME, configurationAnnotationConfigurerBean.getBeanDefinition());
        BeanDefinitionBuilder environmentAnnotationConfigurerBean = BeanDefinitionBuilder.rootBeanDefinition(EnvironmentAnnotationConfigurer.class);
        environmentAnnotationConfigurerBean.addConstructorArgValue(assemblyContextResolver);
        parserContext.getRegistry().registerBeanDefinition(CONSTRETTO_ENVIRONMENT_ANNOTATION_BEAN_NAME, environmentAnnotationConfigurerBean.getBeanDefinition());
    }


    private class ConfigurationDefinitionParser implements BeanDefinitionParser {

        public BeanDefinition parse(Element element, ParserContext parserContext) {
            ConfigurationContextResolver configurationContextResolver = processConfigurationContextResolverTag(DomUtils.getChildElementByTagName(element, "configuration-context-resolver"), parserContext);
            AssemblyContextResolver assemblyContextResolver = processAssemblyContextResolverTag(DomUtils.getChildElementByTagName(element, "assembly-context-resolver"), parserContext);

            ConstrettoConfiguration configuration = buildConfig(element, configurationContextResolver);

            processAnnotationConfig(element, configuration, assemblyContextResolver, parserContext);
            processPropertyPlaceHolder(element, configuration, parserContext);

            BeanDefinitionBuilder configurationFactoryBean = BeanDefinitionBuilder.rootBeanDefinition(ConstrettoConfigurationFactoryBean.class);
            configurationFactoryBean.addConstructorArgValue(configuration);
            parserContext.getRegistry().registerBeanDefinition(CONSTRETTO_CONFIGURATION_BEAN_NAME, configurationFactoryBean.getBeanDefinition());

            return null;
        }


        @SuppressWarnings("unchecked")
        private ConstrettoConfiguration buildConfig(Element element, ConfigurationContextResolver configurationContextResolver) {
            ConstrettoBuilder builder = new ConstrettoBuilder(configurationContextResolver);
            Element storeElement = DomUtils.getChildElementByTagName(element, "stores");
            if (storeElement != null) {
                List<Element> stores = getAllChildElements(storeElement);
                for (Element store : stores) {
                    String tagName = store.getLocalName();
                    if ("properties-store".equals(tagName)) {
                        ConstrettoBuilder.PropertiesStoreBuilder propertiesBuilder = builder.createPropertiesStore();
                        List<Element> resources = DomUtils.getChildElementsByTagName(store, "resource");
                        for (Element resource : resources) {
                            String location = resource.getAttribute("location");
                            propertiesBuilder.addResource(new DefaultResourceLoader(this.getClass().getClassLoader()).getResource(location));
                        }
                        propertiesBuilder.done();
                    } else if ("ini-store".equals(tagName)) {
                        ConstrettoBuilder.IniFileConfigurationStoreBuilder iniBuilder = builder.createIniFileConfigurationStore();
                        List<Element> resources = DomUtils.getChildElementsByTagName(store, "resource");
                        for (Element resource : resources) {
                            String location = resource.getAttribute("location");
                            iniBuilder.addResource(new DefaultResourceLoader(this.getClass().getClassLoader()).getResource(location));
                        }
                        iniBuilder.done();
                    } else if ("system-properties-store".equals(tagName)) {
                        builder.createSystemPropertiesStore();
                    } else if ("object-store".equals(tagName)) {
                        ConstrettoBuilder.ObjectConfigurationStoreBuilder objectBuilder = builder.createObjectConfigurationStore();
                        List<Element> objects = DomUtils.getChildElementsByTagName(store, "object");
                        for (Element object : objects) {
                            String clazz = object.getAttribute("class");
                            try {
                                objectBuilder.addObject(Class.forName(clazz).newInstance());
                            } catch (Exception e) {
                                throw new IllegalStateException("Could not instansiate configuration source object with class [" + clazz + "]");
                            }
                        }
                        objectBuilder.done();
                    }
                }
            }

            return builder.getConfiguration();
        }

        private void processPropertyPlaceHolder(Element element, ConstrettoConfiguration configuration, ParserContext parserContext) {
            String propertyPlaceholderAttribute = element.getAttribute("property-placeholder");
            Element propertyPlaceholderElement = DomUtils.getChildElementByTagName(element, "property-placeholder-configurer");
            boolean enabled = propertyPlaceholderAttribute != null ? Boolean.valueOf(propertyPlaceholderAttribute) : true;
            if (enabled) {
                createPropertyPlaceholder(configuration, parserContext, propertyPlaceholderElement);
            }
        }


        private void processAnnotationConfig(Element element, ConstrettoConfiguration configuration, AssemblyContextResolver assemblyContextResolver, ParserContext parserContext) {
            String annotationConfigAttribute = element.getAttribute("annotation-config");
            boolean enabled = annotationConfigAttribute != null ? Boolean.valueOf(annotationConfigAttribute) : true;
            if (enabled) {
                createAnnotationConfigBean(configuration, assemblyContextResolver, parserContext);
            }
        }

        private ConfigurationContextResolver processConfigurationContextResolverTag(Element element, ParserContext parserContext) {
            if (element == null) {
                BeanDefinitionBuilder contextResolverBean;
                contextResolverBean = BeanDefinitionBuilder.rootBeanDefinition(DefaultConfigurationContextResolver.class);
                parserContext.getRegistry().registerBeanDefinition(CONFIGURATION_CONTEXT_RESOLVER_NAME, contextResolverBean.getBeanDefinition());
                return new DefaultConfigurationContextResolver();
            } else {
                String clazz = element.getAttribute("class");
                BeanDefinitionBuilder contextResolverBean = BeanDefinitionBuilder.rootBeanDefinition(clazz);
                parserContext.getRegistry().registerBeanDefinition(CONFIGURATION_CONTEXT_RESOLVER_NAME, contextResolverBean.getBeanDefinition());
                try {
                    return (ConfigurationContextResolver) Class.forName(clazz).newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Could not instansiate configuration context resolver with class [" + clazz + "]", e);
                }
            }
        }

        private AssemblyContextResolver processAssemblyContextResolverTag(Element element, ParserContext parserContext) {
            if (element == null) {
                BeanDefinitionBuilder contextResolverBean;
                contextResolverBean = BeanDefinitionBuilder.rootBeanDefinition(DefaultAssemblyContextResolver.class);
                parserContext.getRegistry().registerBeanDefinition(ENVIRONMENT_CONTEXT_RESOLVER_NAME, contextResolverBean.getBeanDefinition());
                return new DefaultAssemblyContextResolver();
            } else {
                String clazz = element.getAttribute("class");
                BeanDefinitionBuilder contextResolverBean = BeanDefinitionBuilder.rootBeanDefinition(clazz);
                parserContext.getRegistry().registerBeanDefinition(ENVIRONMENT_CONTEXT_RESOLVER_NAME, contextResolverBean.getBeanDefinition());
                try {
                    return (AssemblyContextResolver) Class.forName(clazz).newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Could not instansiate assembly context resolver with class [" + clazz + "]", e);
                }
            }
        }

        public List<Element> getAllChildElements(Element element) {
            Assert.notNull(element, "Element must not be null");
            NodeList childNodes = element.getChildNodes();
            List<Element> childElements = new ArrayList<Element>();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    childElements.add((Element) node);
                }
            }
            return childElements;
        }
    }

    private class ImportDefinitionParser implements BeanDefinitionParser {

        public BeanDefinition parse(Element element, ParserContext parserContext) {
            String targetEnvironmentsCsv = element.getAttribute("environments");
            String resourcePath = element.getAttribute("resource");
            List<String> targetEnvironments = ConstrettoUtils.fromCSV(targetEnvironmentsCsv);

            AssemblyContextResolver assemblyContextResolver = null;

            if (parserContext.getRegistry().containsBeanDefinition(ENVIRONMENT_CONTEXT_RESOLVER_NAME)) {
                BeanDefinition environmentContextResolverBeanDefinition = parserContext.getRegistry().getBeanDefinition(ENVIRONMENT_CONTEXT_RESOLVER_NAME);
                String environmentResolverClassName = environmentContextResolverBeanDefinition.getBeanClassName();
                try {
                    assemblyContextResolver = (AssemblyContextResolver) Class.forName(environmentResolverClassName).newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Could not instansiate assembly context resolver with class [" + environmentResolverClassName + "]", e);
                }
            } else {
                assemblyContextResolver = new DefaultAssemblyContextResolver();
            }

            List<String> assemblyContext = assemblyContextResolver.getAssemblyContext();
            targetEnvironments.retainAll(assemblyContext);
            boolean include = !targetEnvironments.isEmpty();
            if (include) {
                parserContext.getReaderContext().getReader().loadBeanDefinitions(resourcePath);
            }
            return null;
        }

    }

    private class PropertyPlaceHolderDefinitionParser implements BeanDefinitionParser {
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            createPropertyPlaceholder(null, parserContext, element);
            return null;
        }
    }

    private class AnnotationConfigDefinitionParser implements BeanDefinitionParser {
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            createAnnotationConfigBean(null, null, parserContext);
            return null;
        }
    }
}
