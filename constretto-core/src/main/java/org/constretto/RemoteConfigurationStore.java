package org.constretto;

import org.constretto.model.RemoteProperty;

import java.util.List;

public interface RemoteConfigurationStore {
    
    RemoteProperty getValue(String key, List<String> tag);

}
