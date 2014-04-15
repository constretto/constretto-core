package org.constretto.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.constretto.ConstrettoBuilder;
import org.constretto.model.Resource;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationManagerModuleTest {

    @Test
    public void injectingConstrettoConfigWhenGuiceCreatesInstanceOfGivenClass() {
        Injector injector = Guice.createInjector(new ConfigurationManagerModule(
                new ConstrettoBuilder()
                        .createPropertiesStore()
                        .addResource(Resource.create("classpath:config.properties"))
                        .done()
                        .getConfiguration()
        ));

        final ConstrettoConfig instance = injector.getInstance(ConstrettoConfig.class);

        Assert.assertEquals("value", instance.getValue());
    }
}