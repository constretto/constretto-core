package org.constretto.internal.store;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.util.Properties;

/**
 * Has support for jasypt encrypted properties.
 * See  <a href="http://www.jasypt.org/cli.html">Jasypt CLI</a> and <a href="http://www.jasypt.org/encrypting-configuration.html">Jasypt Configuration Files</a>
 * @author Ole-Martin Mørk (olemartin@openadex.com)
 */
public class EncryptedPropertiesStore extends PropertiesStore {
    private StandardPBEStringEncryptor encryptor;

    /**
     * Creates a new instance
     * @param passwordProperty the name of the system property to read from
     */
    public EncryptedPropertiesStore(String passwordProperty) {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(getPassword(passwordProperty));
    }

    private String getPassword(String property) {
        return System.getProperty(property);
    }

    /**
     * Uses jasypt to parse the properties
     * @param props the properties currently read
     * @return an instance of {@link org.jasypt.properties.EncryptableProperties}
     */
    @Override
    protected Properties parseProperties(Properties props) {
        return new EncryptableProperties(props, encryptor);
    }
}