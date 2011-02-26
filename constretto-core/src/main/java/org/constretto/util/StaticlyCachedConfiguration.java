/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.util;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.internal.store.IniFileConfigurationStore;
import org.constretto.internal.store.PropertiesStore;
import org.constretto.model.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 *         Based on a configurationfactory used, and donated by FinnTech.
 */
public class StaticlyCachedConfiguration {
    private static Map<CacheKey, ConstrettoConfiguration> cache = new HashMap<CacheKey, ConstrettoConfiguration>();
    private static int cacheHits = 0;

    public static ConstrettoConfiguration config(String... locations) {
        CacheKey key = new CacheKey(locations);
        if (cache.containsKey(key)) {
            cacheHits++;
            return cache.get(key);
        }
        ConstrettoBuilder builder = new ConstrettoBuilder();
        IniFileConfigurationStore iniFileConfigurationStore = new IniFileConfigurationStore();
        PropertiesStore propertyFileConfigurationStore = new PropertiesStore();


        for (String location : locations) {
            if (location.toLowerCase().endsWith(".ini")) {
                iniFileConfigurationStore.addResource(new Resource(location));
            } else if (location.toLowerCase().endsWith(".properties")) {
                propertyFileConfigurationStore.addResource(new Resource(location));
            }
        }
        builder = builder.addConfigurationStore(iniFileConfigurationStore);
        builder = builder.addConfigurationStore(propertyFileConfigurationStore);
        ConstrettoConfiguration configuration = builder.getConfiguration();
        cache.put(key, configuration);
        return configuration;
    }

    public static int cacheHits() {
        return cacheHits;
    }

    private static class CacheKey {
        String key;

        public CacheKey(String[] locations) {
            key = "";
            for (String location : locations) {
                key += location;
            }
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (key != null ? !key.equals(cacheKey.key) : cacheKey.key != null) return false;

            return true;
        }

        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }
    }
}
