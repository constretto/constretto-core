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
package org.constretto.internal.store;

import static junit.framework.Assert.assertEquals;

import java.util.Collection;

import org.constretto.ConfigurationStore;
import org.constretto.Constretto;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.ConfigurationSource;
import org.constretto.internal.store.helper.DefaultCustomerDataSourceConfigurer;
import org.constretto.internal.store.helper.DevelopmentCustomerDataSourceConfigurer;
import org.constretto.internal.store.helper.GenericDataSourceConfigurer;
import org.constretto.internal.store.helper.ProductionCustomerDataSourceConfigurer;
import org.constretto.model.PropertySet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ObjectConfigurationStoreTest extends AbstractConfigurationStoreTest {

    private final ObjectConfigurationStore store = new ObjectConfigurationStore();

    @Before
    public void setUp() {
        store.addObject(new DefaultCustomerDataSourceConfigurer())
             .addObject(new GenericDataSourceConfigurer())
             .addObject(new DevelopmentCustomerDataSourceConfigurer())
             .addObject(new ProductionCustomerDataSourceConfigurer());
    }
    
    
    /**
     * Tests to run for configuration stores.. 
     * this goes for all stores. tests should have one abstract testcase with all the tests, 
     * and subclasses only do setup of the store.
     * 
     * 1. That the configuration is loaded as expected
     * 2. That placeholders in the config is resolved properly
     * 3. When properties exists in more than one "resource" inside a store, the one listed last overrides any previous values
     * 4. When properties exists in more than one store, the one listed in the last store overrides any previuos values
     * 5. When a key is labeled, the most spesific one wins. That is the one listed first in the lables list from the resolver
     * 6. Circular key/value replacements should raise constretto exception.
     * 7. All native types should have a converter
     * 8. Configuration should be navigated via the at() method
     * 9. Objects should be configured using the as() method
     * 10. Already instansiated objects should have config injected with the applyOn() method (@Configuration annotated fields, using their setters if available)
     */
    

    @Test
    public void loadConfiguration() {
        Collection<PropertySet> propertySets = store.load();
        assertEquals(3, propertySets.size());
    }

    @Test
    public void givenLabelDevelopmentThenProviderChoosesDevelopmentValues() {
        ConstrettoConfiguration configuration = new Constretto().addConfigurationStore(store).done().addLabel("development")
                .getConfiguration().at("datasources.customer");
        assertEquals("development-url", configuration.evaluateToString("url"));
    }

    @Test
    @Ignore
    public void givenNoLabelThenProviderChoosesDefaultValues() {
        ConstrettoConfiguration configuration = new Constretto().addConfigurationStore(store).done().getConfiguration().at(
                "datasources.customer");
        assertEquals("default-url", configuration.evaluateToString("url"));
    }

    @Test
    public void givenLabelProductionThenProviderChoosesProductionValues() {
        ConstrettoConfiguration configuration = new Constretto().addConfigurationStore(store).done().addLabel("production")
                .getConfiguration().at("datasources.customer");
        assertEquals("production-url", configuration.evaluateToString("url"));
    }

    @Test
    public void givenNoLabelAndFromRootThenProviderChoosesGenericValues() {
        ConstrettoConfiguration configuration = new Constretto().addConfigurationStore(store).done().getConfiguration();
        assertEquals("generic-url", configuration.evaluateToString("url"));
    }


	@Override
	protected ConfigurationStore getStore() {
		ObjectConfigurationStore ocStore = new ObjectConfigurationStore();
		ocStore.addObject(new TestConfig("user0"));
		ocStore.addObject(new ProductionTestConfig("user1"));
		ocStore.addObject(new SysTestTestConfig("user2"));
		return ocStore;
	}

	@ConfigurationSource(basePath = "somedb")
    public static class TestConfig {

        private String username;

        TestConfig(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

    }

    @ConfigurationSource(label = "production", basePath = "somedb")
    public static class ProductionTestConfig extends TestConfig {

        public ProductionTestConfig(String username) {
            super(username);
        }

    }

    @ConfigurationSource(label = "systest", basePath = "somedb")
    public static class SysTestTestConfig extends TestConfig {

        public SysTestTestConfig(String username) {
            super(username);
        }

    }

}
