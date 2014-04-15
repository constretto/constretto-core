package org.constretto.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.constretto.ConstrettoConfiguration;

public class ConfigurationManagerModule extends AbstractModule {
    private final ConstrettoConfiguration constrettoConfiguration;

    public ConfigurationManagerModule(final ConstrettoConfiguration constrettoConfiguration) {
        this.constrettoConfiguration = constrettoConfiguration;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new ConstrettoTypeListener(constrettoConfiguration));
        bind(ConstrettoConfiguration.class).toInstance(constrettoConfiguration);
    }
}