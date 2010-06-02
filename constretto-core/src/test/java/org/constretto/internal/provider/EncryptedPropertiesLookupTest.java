package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static junit.framework.Assert.assertEquals;

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
                .addResource(new ClassPathResource(("encrypted.properties")))
                .done();
        ConstrettoConfiguration config = constrettoBuilder.getConfiguration();
        assertEquals("Testing a property", config.evaluateToString("encrypted_property"));
    }
}
