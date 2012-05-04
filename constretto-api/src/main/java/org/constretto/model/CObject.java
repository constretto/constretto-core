package org.constretto.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CObject extends CValue {
    private final Map<String, CValue> data;

    public CObject(Map<String, CValue> data) {
        this.data = data;
    }

    public Map<String, CValue> data() {
        return data;
    }

    @Override
    public Set<String> referencedKeys() {
        Set<String> referencedKeys = new HashSet<String>();
        for (CValue value : data.values()) {
            referencedKeys.addAll(value.referencedKeys());
        }
        return referencedKeys;
    }

    @Override
    public void replace(String key, String resolvedValue) {
        for (CValue value : data.values()) {
            value.replace(key, resolvedValue);
        }
    }

    @Override
    public String toString() {
        String value = "{";
        for (Map.Entry<String, CValue> entry : data.entrySet()) {
            value += entry.getKey() + ":" + entry.getValue().toString() + ", ";
        }
        if (value.endsWith(", ")){
            value = value.substring(0,value.length()-2);
        }
        return value + "}";
    }
}
