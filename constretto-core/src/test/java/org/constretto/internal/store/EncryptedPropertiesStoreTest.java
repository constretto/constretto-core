package org.constretto.internal.store;

import org.constretto.model.Resource;
import org.constretto.model.TaggedPropertySet;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:zapodot@gmail.com">zapodot</a>
 */
public class EncryptedPropertiesStoreTest {

    public static final String PROPERTY_KEY = "encryptionKey";
    public static final String ENCRYPTION_KEY = "constretto";
    public static final String KEY = "key";
    public static final String UNENCRYPTED_VALUE = "a value";

    @Rule
    public ProvideSystemProperty provideSystemProperty = new ProvideSystemProperty(PROPERTY_KEY, ENCRYPTION_KEY);

    private String encryptedValue;
    private Properties encryptedProperties;

    @Before
    public void setUp() throws Exception {
        final BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(ENCRYPTION_KEY);
        encryptedValue = basicTextEncryptor.encrypt(UNENCRYPTED_VALUE);
        encryptedProperties = new Properties();
        encryptedProperties.put(KEY, encryptedValue);

    }

    @Test
    public void testParseProperties() throws Exception {

        final EncryptedPropertiesStore encryptedPropertiesStore = new EncryptedPropertiesStore(PROPERTY_KEY);
        final Properties encrypted = encryptedPropertiesStore.parseProperties(encryptedProperties);
        assertEquals(encryptedValue, encrypted.getProperty(KEY));

    }

    @Test
    public void testParseConfigurationFromResource() throws Exception {

        final EncryptedPropertiesStore encryptedPropertiesStore = new EncryptedPropertiesStore(PROPERTY_KEY);
        final List<TaggedPropertySet> taggedPropertySets = encryptedPropertiesStore
                .addResource(Resource.create("classpath:encrypted.properties"))
                .parseConfiguration();
        assertEquals("Testing a property", taggedPropertySets.get(0).getProperties().get("encrypted_property"));

    }
}
