package org.constretto.spring.configuration.helper;

import org.constretto.annotation.Configure;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class BeanWithEncryptedProperty {

    private String encrypted_property;
    private String encrypted_property_II;

    @Configure
    public void configure(String encrypted_property){
        this.encrypted_property = encrypted_property;
    }

    public String getEncrypted_property() {
        return encrypted_property;
    }

    public String getEncrypted_property_II() {
        return encrypted_property_II;
    }

    public void setEncrypted_property_II(String encrypted_property_II) {
        this.encrypted_property_II = encrypted_property_II;
    }
}
