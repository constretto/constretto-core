package org.constretto.spring.configuration;

import org.constretto.annotation.Configure;

/**
 * This bean can never be have values injected into the constructor as there can only be one
 * @Configure annotated constructor for each class
 *
 * @author zapodot
 */
public class IllegalNumberOfConfiguredConstructorsBean {

    private String keyOne;

    @Configure
    public IllegalNumberOfConfiguredConstructorsBean(final String keyOne) {
        this.keyOne = keyOne;
    }

    @Configure
    public IllegalNumberOfConfiguredConstructorsBean(final Integer numberOne) {

    }
}
