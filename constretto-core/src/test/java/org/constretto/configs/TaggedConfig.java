package org.constretto.configs;

import org.constretto.annotation.ConfigurationSource;

/**
 * @author sondre
 */
@ConfigurationSource(tag = TaggedConfig.TAG)
public class TaggedConfig extends Config {

    public static final String TAGGED_VALUE = "taggedValue";
    public static final String TAG = "tag";

    @Override
    public String getValue() {
        return TAGGED_VALUE;
    }
}
