package org.constretto;

import org.constretto.configs.TaggedConfig;
import org.constretto.configs.UntaggedConfig;
import org.constretto.resolver.ConfigurationContextResolver;
import org.constretto.resolver.PredefinedConfigurationContextResolver;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author zapodot
 */
public class ConstrettoBuilderTest {

    public static final ConfigurationContextResolver STATIC_CONTEXT_RESOLVER = PredefinedConfigurationContextResolver.usingTags(TaggedConfig.TAG);

    @Test
    public void testWithSystemProperties() throws Exception {
        final Map<String, String> configuration = ConstrettoBuilder.withSystemProperties().getConfiguration().asMap();
        assertFalse(configuration.isEmpty());

        assertEquals(configuration.size(), ConstrettoBuilder.withSystemProperties(STATIC_CONTEXT_RESOLVER).getConfiguration().asMap().size());

    }


    @Test
    public void testEmpty() throws Exception {
        final ConstrettoConfiguration emptyConfiguration = ConstrettoBuilder.empty().getConfiguration();
        assertTrue(emptyConfiguration.asMap().isEmpty());

        assertEquals(emptyConfiguration.asMap().size(), ConstrettoBuilder.empty(STATIC_CONTEXT_RESOLVER).getConfiguration().asMap().size());
    }

    @Test
    public void testFromExistingConfiguration() throws Exception {
        final ConstrettoConfiguration preExistingConfiguration = ConstrettoBuilder.empty(STATIC_CONTEXT_RESOLVER)
                .createObjectConfigurationStore()
                .addObject(new UntaggedConfig())
                .addObject(new TaggedConfig())
                .done().getConfiguration();

        assertEquals(TaggedConfig.TAGGED_VALUE, preExistingConfiguration.evaluateToString("value"));
        final ConstrettoConfiguration configuration = ConstrettoBuilder.fromExistingConfiguration(preExistingConfiguration, PredefinedConfigurationContextResolver.empty()).getConfiguration();
        assertEquals(TaggedConfig.TAGGED_VALUE, configuration.evaluateToString("value"));

    }


}