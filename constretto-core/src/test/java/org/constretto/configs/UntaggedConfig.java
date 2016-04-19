package org.constretto.configs;

import org.constretto.annotation.ConfigurationSource;

/**
 * @author sondre
 */
@ConfigurationSource
public class UntaggedConfig extends Config {
    public static final String UNTAGGED_VALUE = "untaggedValue";

    @Override
    public String getValue() {
        return UNTAGGED_VALUE;
    }
}
