package org.constretto.model;

import java.util.Map;

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
    public void replace(String key, CValue resolvedValue) {
        for (CValue value : data.values()) {
            value.replace(key, resolvedValue);
        }
    }
}
