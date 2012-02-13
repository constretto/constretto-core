package org.constretto;

import org.constretto.model.ClassPathResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstrettoBuilderTest {

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

}
