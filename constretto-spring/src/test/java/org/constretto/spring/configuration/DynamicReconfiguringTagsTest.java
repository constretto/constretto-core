package org.constretto.spring.configuration;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class DynamicReconfiguringTagsTest {
    private ConstrettoConfiguration config;

    @Before
    public void createConfiguration() {
        config = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(new ClassPathResource("properties/test1.properties"))
                .done().getConfiguration();
    }

    @Test
    public void whenSettingTagsRuntimeVariablesShouldBeResolvedCorrectlyWhenUsingJavaApi() {
        assertEquals("default value", config.evaluateToString("stagedKey"));
        config.addTag("development");
        assertEquals("development value", config.evaluateToString("stagedKey"));
    }

    @Test
    public void whenSettingTagsRuntimeObjectsInjectedWithConfigurationShouldBeResolvedCorrectly() throws InterruptedException {
        ConfiguredClass configuredClass = new ConfiguredClass();
        config.on(configuredClass);
        assertEquals("default value", configuredClass.stagedKey);
        assertEquals("default", configuredClass.stagedVariable);
        config.addTag("test");
        assertEquals("test value", configuredClass.stagedKey);
        assertEquals("test", configuredClass.stagedVariable);
        config.removeTag("test");
        assertEquals("default value", configuredClass.stagedKey);
        assertEquals("default", configuredClass.stagedVariable);
        configuredClass = null;
        config.addTag("linux");
        Assert.assertNull(configuredClass);
    }


    private class ConfiguredClass {
        @Configuration
        private String stagedVariable;
        private String stagedKey;


        @Configure
        public void configure(String stagedKey) {
            this.stagedKey = stagedKey;
        }
    }
}
