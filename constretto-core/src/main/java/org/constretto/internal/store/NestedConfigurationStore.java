package org.constretto.internal.store;

import org.constretto.ConfigurationStore;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.TaggedPropertySet;

import java.util.Arrays;
import java.util.Collection;

/**
 * A special kind of ConfigurationStore that contains a nested ConstrettoConfiguration instance.
 *
 * @author zapodot
 * @since 3.0
 */
public class NestedConfigurationStore implements ConfigurationStore {

    private ConstrettoConfiguration configuration;

    public NestedConfigurationStore(final ConstrettoConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Collection<TaggedPropertySet> parseConfiguration() {

        return Arrays.asList(new TaggedPropertySet(configuration.asMap(), getClass()));
    }
}
