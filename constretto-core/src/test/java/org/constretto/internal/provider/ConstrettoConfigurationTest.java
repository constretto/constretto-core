package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class ConstrettoConfigurationTest {
    private ConstrettoConfiguration constrettoConfiguration;
    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();


    @Before
    public void loadConfiguration() {
        constrettoConfiguration = new ConstrettoBuilder()
                .addCurrentTag("production")
                .createPropertiesStore()
                .addResource(resourceLoader.getResource("classpath:test.properties"))
                .done()
                .getConfiguration();
    }

    @Test
    public void lookupCompositeElementUsingTagAndLookupKeyHasNoTag() {
        Assert.assertEquals("http://constretto.org/child", constrettoConfiguration.evaluateToString("url.child"));
    }


}

