package org.constretto;

import org.constretto.model.ClassPathResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstrettoConfigurationTest {

    @Test
    public void constrettoShouldWorkWithNonExistingResources() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .addResource(new ClassPathResource("IdoNoExist.properties"))
                .done()
                .getConfiguration();
        assertNotNull(configuration);
    }


    @Test
    public void youShouldBeAbleToGetCurrentEnvironmentTroughTheAPI() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .done()
                .getConfiguration();
        assertEquals(0, configuration.getCurrentTags().size());
        configuration.prependTag("dev");
        assertEquals(1, configuration.getCurrentTags().size());
        configuration.prependTag("local");
        assertEquals(2, configuration.getCurrentTags().size());
    }

    @Test
    public void iteratingSimplePropertiesShouldWork() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .done()
                .addCurrentTag("production")
                .getConfiguration();
        List<String>props = new ArrayList<String>();
        for (Property property : configuration) {
            String propString = property.getKey() + " = " + property.getValue();
            props.add(propString);
        }
        assertEquals(4,props.size());
    }

    @Test
    public void iteratingPropertiesContainingArraysAndMapsShouldWork() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test-with-array-and-map.properties"))
                .done()
                .getConfiguration();
        List<String>props = new ArrayList<String>();
        for (Property property : configuration) {
            String propString = property.getKey() + " = " + property.getValue();
            props.add(propString);
        }
        assertEquals(2,props.size());
    }

}
