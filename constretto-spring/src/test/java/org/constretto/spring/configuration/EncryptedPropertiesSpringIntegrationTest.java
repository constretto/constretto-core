package org.constretto.spring.configuration;

import org.constretto.spring.configuration.helper.BeanWithEncryptedProperty;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EncryptedPropertiesSpringIntegrationTest {
    @Autowired
    BeanWithEncryptedProperty beanWithEncryptedProperty;

    @BeforeClass
    public static void setupSeed() {
        System.setProperty("SEED", "constretto");
    }

    @Test
    public void encryptedPropertiesCorrectlyInjectedWithSpring(){
        assertEquals("Testing a property",beanWithEncryptedProperty.getEncrypted_property());
        assertEquals(beanWithEncryptedProperty.getEncrypted_property(),beanWithEncryptedProperty.getEncrypted_property_II());
    }


    @AfterClass
    public static void removeSeed() {
        System.setProperty("SEED", "constretto");
    }
}
