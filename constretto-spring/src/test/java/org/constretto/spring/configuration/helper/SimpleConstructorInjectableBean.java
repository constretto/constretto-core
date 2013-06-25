package org.constretto.spring.configuration.helper;

import org.constretto.annotation.Configure;

/**
 *
 */
public class SimpleConstructorInjectableBean {

    private String key1;

    @Configure
    public SimpleConstructorInjectableBean(final String key1) {
        this.key1 = key1;
    }

    public String getKey1() {
        return key1;
    }
}
