package org.constretto;

import org.constretto.exception.ConstrettoExpressionException;
import org.constretto.model.ClassPathResource;
import org.constretto.model.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Test(expected = ConstrettoExpressionException.class) //TODO perhaps a more specific exception
    public void shouldThrowExceptionIfTryingToMapWithoutNeededAllTagsProvided() throws Exception {
        new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .done()
                .getConfiguration()
                .asMap();
    }

    @Test
    public void youShouldBeAbleToGetConfigurationAsAMap() throws Exception {
        Map<String, String> map = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .done()
                .addCurrentTag("production")
                .getConfiguration()
                .asMap();
        assertEquals(4, map.size());
    }

    @Test
    public void testAsIterator() throws Exception {
        final ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createPropertiesStore()
                .addResource(new ClassPathResource("test.properties"))
                .done()
                .addCurrentTag("production")
                .getConfiguration();

        int i = 0;
        for (final Property property : configuration) {
            i++;
        }
        assertEquals(4, i);
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

    @Test
    public void getConfiguration_iniStoreWithoutOverride() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createIniFileConfigurationStore().addResource(Resource.create("classpath:cache1.ini")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key1"), "value1");
    }

    @Test
    public void getConfiguration_iniStoreWithOneOverride() {
        System.setProperty(ConstrettoBuilder.OVERRIDES, "classpath:cache1-override1.ini");
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createIniFileConfigurationStore().addResource(Resource.create("classpath:cache1.ini")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key1"), "value1-override1");
        System.clearProperty(ConstrettoBuilder.OVERRIDES);
    }

    @Test
    public void getConfiguration_iniStoreWithTwoOverrides() {
        System.setProperty(ConstrettoBuilder.OVERRIDES, "classpath:cache1-override1.ini,classpath:cache1-override2.ini");
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createIniFileConfigurationStore().addResource(Resource.create("classpath:cache1.ini")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key1"), "value1-override2");
        System.clearProperty(ConstrettoBuilder.OVERRIDES);
    }

    @Test
    public void getConfiguration_propertyStoreWithoutOverride() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore().addResource(Resource.create("classpath:cache3.properties")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key3"), "value3");
    }

    @Test
    public void getConfiguration_propertyStoreWithOneOverride() {
        System.setProperty(ConstrettoBuilder.OVERRIDES, "classpath:cache3-override1.properties");
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore().addResource(Resource.create("classpath:cache3.properties")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key3"), "value3-override1");
        System.clearProperty(ConstrettoBuilder.OVERRIDES);
    }

    @Test
    public void getConfiguration_propertyStoreWithTwoOverrides() {
        System.setProperty(ConstrettoBuilder.OVERRIDES, "classpath:cache3-override1.properties,classpath:cache3-override2.properties");
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore().addResource(Resource.create("classpath:cache3.properties")).done()
                .getConfiguration();

        assertEquals(configuration.evaluateToString("key3"), "value3-override2");
        System.clearProperty(ConstrettoBuilder.OVERRIDES);
    }
}
