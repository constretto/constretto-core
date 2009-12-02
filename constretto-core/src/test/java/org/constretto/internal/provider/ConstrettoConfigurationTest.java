package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.Property;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;
import java.util.ArrayList;

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
        assertEquals("http://constretto.org/child", constrettoConfiguration.evaluateToString("url.child"));
    }

    @Test
    public void configurationIterator(){
        List<Property> expected = new ArrayList<Property>(){{
            add(new Property("somedb.username","user1"));
            add(new Property("datasources.customer.password","password"));
            add(new Property("url.child","http://constretto.org/child"));
            add(new Property("base-url","http://constretto.org"));
        }};

        List<Property> actual = new ArrayList<Property>();
        for (Property property : constrettoConfiguration) {
            actual.add(property);
        }
        assertEquals(expected.size(),actual.size());
        assertTrue(actual.containsAll(expected));
    }


}

