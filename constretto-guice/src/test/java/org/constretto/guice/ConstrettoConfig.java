package org.constretto.guice;

import org.constretto.annotation.Configuration;

public class ConstrettoConfig {

    @Configuration("value")
    private String value;

    public String getValue() {
        return value;
    }
}
