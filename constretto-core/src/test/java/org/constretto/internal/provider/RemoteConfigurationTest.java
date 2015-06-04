package org.constretto.internal.provider;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.RemoteConfigurationStore;
import org.constretto.model.ClassPathResource;
import org.constretto.model.RemoteProperty;
import org.constretto.model.StringResource;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RemoteConfigurationTest {
    
    @Test
    public void simpleRemoteLookup() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .addRemoteConfigurationStore("someschema", new DummyRemote())
                .getConfiguration();

        assertEquals("key1-value", configuration.evaluateToString("someschema:key1"));
    }
    
    @Test
    public void interpolateWithRemoteLookup() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore()
                    .addResource(new StringResource("prop.key:http://#{someschema:key1}"))
                    .done()
                .addRemoteConfigurationStore("someschema", new DummyRemote())
                .getConfiguration();

        assertEquals("http://key1-value", configuration.evaluateToString("prop.key"));
    }

    public static class DummyRemote implements RemoteConfigurationStore {

        @Override
        public RemoteProperty getValue(String key, List<String> tag) {
            if (key.equals("key1")) {
                return RemoteProperty.property("key1-value");
            }
            return RemoteProperty.propertyNotFound();
        }
    }
}
