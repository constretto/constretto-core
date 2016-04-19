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
package org.constretto;

import org.constretto.internal.DefaultConstrettoConfiguration;
import org.constretto.internal.resolver.DefaultConfigurationContextResolver;
import org.constretto.internal.store.*;
import org.constretto.internal.store.ldap.LdapConfigurationStoreBuilder;
import org.constretto.model.*;
import org.constretto.resolver.ConfigurationContextResolver;

import javax.naming.directory.DirContext;
import java.util.*;

/**
 * Provides a fluent Java api to build a constretto configuration object.
 *
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoBuilder {

    public static final String OVERRIDES = "CONSTRETTO_OVERRIDES";
    private final List<ConfigurationStore> configurationStores;
    private final List<String> tags;
    private final boolean enableSystemProps;
    private final Parser parser = new GsonParser();

    /**
     * @deprecated use the factory method @link{#empty} instead
     */
    @Deprecated
    public ConstrettoBuilder() {
        this(new DefaultConfigurationContextResolver(), true);
    }

    /**
     * @param enableSystemProps a boolean that decides whether system properties are added to the configuration or not
     * @deprecated use the factory method @link{#empty(boolean)} instead
     */
    @Deprecated
    public ConstrettoBuilder(boolean enableSystemProps) {
        this(new DefaultConfigurationContextResolver(), enableSystemProps);
    }


    public ConstrettoBuilder(ConfigurationContextResolver configurationContextResolver, boolean enableSystemProps) {
        this.configurationStores = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.enableSystemProps = enableSystemProps;
        for (String tag : configurationContextResolver.getTags()) {
            addCurrentTag(tag);
        }
        if (enableSystemProps) {
            configurationStores.add(new SystemPropertiesStore());
        }
    }


    private ConstrettoBuilder(List<ConfigurationStore> configurationStores, List<String> tags, boolean enableSystemProps) {
        this.enableSystemProps = enableSystemProps;
        this.configurationStores = configurationStores;
        this.tags = tags;
    }

    /**
     * Creates a new ConstrettoBuilder with a system properties store added
     *
     * @param contextResolver the contextResolver implementation to use to find out what the context should be
     * @return a new ConstrettoBuilder instance
     */
    public static ConstrettoBuilder withSystemProperties(final ConfigurationContextResolver contextResolver) {
        return new ConstrettoBuilder(contextResolver, true);
    }

    /**
     * Creates a new ConstrettoBuilder with a system properties store added and using
     * the DefaultConfigurationContextResolver to resolve configuration context
     *
     * @return a new ConstrettoBuilder instance
     */
    public static ConstrettoBuilder withSystemProperties() {
        return withSystemProperties(new DefaultConfigurationContextResolver());
    }

    /**
     * Creates an empty ConstrettoBuilder using the DefaultConfigurationContextResolver to resolve
     * the configuration context
     *
     * @return a new ConstrettoBuilder
     */
    public static ConstrettoBuilder empty() {
        return empty(new DefaultConfigurationContextResolver());
    }

    /**
     * Creates an empty ConstrettoBuilder
     *
     * @param configurationContextResolver the ConfigurationContextResolver to use to resolve the context
     * @return a new ConstrettoBuilder
     */
    public static ConstrettoBuilder empty(final ConfigurationContextResolver configurationContextResolver) {
        return new ConstrettoBuilder(configurationContextResolver, false);
    }

    /**
     * Creates a new ConstrettoBuilder instance based on an existing configuration. Configuration keys are considered be tagged with the default tag
     *
     * @param constrettoConfiguration      an existing constrettoConfiguration instance
     * @param configurationContextResolver the configurationContextResolver to use for the resulting constrettoConfiguration
     * @return a new builder with the existing configuration added
     */
    public static ConstrettoBuilder fromExistingConfiguration(final ConstrettoConfiguration constrettoConfiguration, final ConfigurationContextResolver configurationContextResolver) {
        Objects.requireNonNull(constrettoConfiguration, "The \"constrettoConfiguration\" argument can not be null");
        Objects.requireNonNull(configurationContextResolver, "The \"configurationContextResolver\" argument can not be null");
        return new ConstrettoBuilder(configurationContextResolver, false).addExistingConfiguration(constrettoConfiguration);
    }

    /**
     * Creates a new ConstrettoBuilder instance based on an existing constrettoConfiguration using the default ConfigurationContextResolver
     *
     * @param constrettoConfiguration an existing constrettoConfiguration instance
     * @return a new builder with the existing configuration added
     */
    public static ConstrettoBuilder fromExistingConfiguration(final ConstrettoConfiguration constrettoConfiguration) {
        return fromExistingConfiguration(constrettoConfiguration, new DefaultConfigurationContextResolver());
    }


    public ConstrettoConfiguration getConfiguration() {
        addOverrideStores();

        Map<String, List<ConfigurationValue>> configuration = new HashMap<>();
        Collection<TaggedPropertySet> taggedPropertySets = loadPropertySets();
        for (TaggedPropertySet taggedPropertySet : taggedPropertySets) {
            Map<String, String> properties = taggedPropertySet.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (configuration.containsKey(entry.getKey())) {
                    List<ConfigurationValue> values = configuration.get(entry.getKey());
                    if (values == null) {
                        values = new ArrayList<>();
                    }
                    values.add(new ConfigurationValue(parser.parse(entry.getValue()), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);

                } else {
                    List<ConfigurationValue> values = new ArrayList<>();
                    values.add(new ConfigurationValue(parser.parse(entry.getValue()), taggedPropertySet.tag()));
                    configuration.put(entry.getKey(), values);
                }
            }
        }
        return new DefaultConstrettoConfiguration(configuration, tags);
    }

    private void addOverrideStores() {
        String overrideValue = System.getProperty(OVERRIDES);

        if (overrideValue != null) {
            String[] locations = overrideValue.split(",");

            for (String location : locations) {
                addOverrideStore(location);
            }
        }
    }

    private void addOverrideStore(String location) {
        if (location.endsWith(".properties")) {
            PropertiesStoreBuilder propertiesBuilder = createPropertiesStore();
            propertiesBuilder.addResource(Resource.create(location));
            propertiesBuilder.done();
        } else if (location.endsWith(".ini")) {
            IniFileConfigurationStoreBuilder iniBuilder = createIniFileConfigurationStore();
            iniBuilder.addResource(Resource.create(location));
            iniBuilder.done();
        }
    }

    public ConstrettoBuilder addCurrentTag(String tag) {
        tags.add(tag);
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
    }

    /**
     * Add configuration store to the builder
     *
     * @param configurationStore the store to added
     * @return the same ConstrettoBuilder with the given ConfigurationStore added
     */
    public ConstrettoBuilder addConfigurationStore(ConfigurationStore configurationStore) {
        configurationStores.add(configurationStore);
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
    }

    /**
     * Adds an existing configuration to be used as a configuration source
     *
     * @param constrettoConfiguration the configuration to be added
     * @return
     */
    public ConstrettoBuilder addExistingConfiguration(final ConstrettoConfiguration constrettoConfiguration) {
        addConfigurationStore(new NestedConfigurationStore(constrettoConfiguration));
        return this;
    }

    public PropertiesStoreBuilder createPropertiesStore() {
        return new PropertiesStoreBuilder();
    }

    public EncryptedPropertiesStoreBuilder createEncryptedPropertiesStore(String passwordProperty) {
        return new EncryptedPropertiesStoreBuilder(passwordProperty);
    }

    public IniFileConfigurationStoreBuilder createIniFileConfigurationStore() {
        return new IniFileConfigurationStoreBuilder();
    }

    public JsonStoreBuilder createJsonConfigurationStore() {
        return new JsonStoreBuilder();
    }

    public ConstrettoBuilder createSystemPropertiesStore() {
        configurationStores.add(new SystemPropertiesStore());
        return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
    }

    public ObjectConfigurationStoreBuilder createObjectConfigurationStore() {
        return new ObjectConfigurationStoreBuilder();
    }

    public WrappedLdapConfigurationStoreBuilder createLdapConfigurationStore(final DirContext dirContext) {
        return new WrappedLdapConfigurationStoreBuilder(LdapConfigurationStoreBuilder.usingDirContext(dirContext));
    }

    private Collection<TaggedPropertySet> loadPropertySets() {
        List<TaggedPropertySet> taggedPropertySets = new ArrayList<>();
        for (ConfigurationStore configurationStore : configurationStores) {
            taggedPropertySets.addAll(configurationStore.parseConfiguration());
        }
        return taggedPropertySets;
    }

    public YamlStoreBuilder createYamlConfigurationStore() {
        return new YamlStoreBuilder();
    }


//
// Store builders
//

    private interface StoreBuilder {
        ConstrettoBuilder done();
    }

    private abstract class ContributingStoreBuilder implements StoreBuilder {

        abstract ConfigurationStore createStore();

        @Override
        final public ConstrettoBuilder done() {
            configurationStores.add(createStore());
            return new ConstrettoBuilder(configurationStores, tags, enableSystemProps);
        }
    }


    public class PropertiesStoreBuilder extends ContributingStoreBuilder {
        private final PropertiesStore store;

        public PropertiesStoreBuilder() {
            this.store = new PropertiesStore();
        }

        private PropertiesStoreBuilder(PropertiesStore store) {
            this.store = store;
        }

        public PropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new PropertiesStoreBuilder(store);
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }

    public class JsonStoreBuilder extends ContributingStoreBuilder {
        private final JsonStore store;

        public JsonStoreBuilder() {
            this.store = new JsonStore();
        }

        private JsonStoreBuilder(JsonStore store) {
            this.store = store;
        }

        public JsonStoreBuilder addResource(Resource resource, String key, String... tags) {
            return new JsonStoreBuilder(store.addResource(resource, key, tags));
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }

    public class YamlStoreBuilder extends ContributingStoreBuilder {
        private final YamlStore store;

        public YamlStoreBuilder() {
            this.store = new YamlStore();
        }

        private YamlStoreBuilder(YamlStore store) {
            this.store = store;
        }

        public YamlStoreBuilder addResource(Resource resource, String key, String... tags) {
            return new YamlStoreBuilder(store.addResource(resource, key, tags));
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }


    public class EncryptedPropertiesStoreBuilder extends ContributingStoreBuilder {
        private final EncryptedPropertiesStore store;

        public EncryptedPropertiesStoreBuilder(String passwordProperty) {
            store = new EncryptedPropertiesStore(passwordProperty);
        }

        private EncryptedPropertiesStoreBuilder(EncryptedPropertiesStore store) {
            this.store = store;
        }

        public EncryptedPropertiesStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new EncryptedPropertiesStoreBuilder(store);
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }

    public class IniFileConfigurationStoreBuilder extends ContributingStoreBuilder {
        private final IniFileConfigurationStore store;

        public IniFileConfigurationStoreBuilder() {
            store = new IniFileConfigurationStore();
        }

        public IniFileConfigurationStoreBuilder(IniFileConfigurationStore store) {
            this.store = store;
        }

        public IniFileConfigurationStoreBuilder addResource(Resource resource) {
            store.addResource(resource);
            return new IniFileConfigurationStoreBuilder(store);
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }

    public class ObjectConfigurationStoreBuilder extends ContributingStoreBuilder {
        private final ObjectConfigurationStore store;

        public ObjectConfigurationStoreBuilder() {
            store = new ObjectConfigurationStore();
        }

        public ObjectConfigurationStoreBuilder(ObjectConfigurationStore store) {
            this.store = store;
        }

        public ObjectConfigurationStoreBuilder addObject(Object object) {
            store.addObject(object);
            return new ObjectConfigurationStoreBuilder(store);
        }

        @Override
        ConfigurationStore createStore() {
            return store;
        }

    }

    public class WrappedLdapConfigurationStoreBuilder extends ContributingStoreBuilder {

        private LdapConfigurationStoreBuilder ldapConfigurationStoreBuilder;

        public WrappedLdapConfigurationStoreBuilder(final LdapConfigurationStoreBuilder ldapConfigurationStoreBuilder) {
            this.ldapConfigurationStoreBuilder = ldapConfigurationStoreBuilder;
        }

        public WrappedLdapConfigurationStoreBuilder addDsnWithKey(final String key,
                                                                  final String distinguishedName,
                                                                  final String... tags) {
            ldapConfigurationStoreBuilder.addDsnWithKey(key, distinguishedName, tags);
            return this;
        }

        public WrappedLdapConfigurationStoreBuilder addDsn(final String distinguishedName, final String... tags) {
            ldapConfigurationStoreBuilder.addDsn(distinguishedName, tags);
            return this;
        }

        public WrappedLdapConfigurationStoreBuilder addUsingSearch(final String searchBase,
                                                                   final String filter,
                                                                   final String keyAttribute, final String... tags) {
            ldapConfigurationStoreBuilder.addUsingSearch(searchBase, filter, keyAttribute, tags);
            return this;
        }

        @Override
        ConfigurationStore createStore() {
            return ldapConfigurationStoreBuilder.done();
        }

    }


}
