package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EncryptedPropertiesLookupTest {
    private static final String PASSWORD_PROPERTY = "PASSWORD";

    @Test
    public void prepareTests() {
        System.setProperty(PASSWORD_PROPERTY, "constretto");
        ConstrettoBuilder constrettoBuilder = new ConstrettoBuilder();
        constrettoBuilder
                .createEncryptedPropertiesStore(PASSWORD_PROPERTY)
                .addResource(Resource.create("classpath:encrypted.properties"))
                .done();
        ConstrettoConfiguration config = constrettoBuilder.getConfiguration();
        assertEquals("Testing a property", config.evaluateToString("encrypted_property"));
    }
}
