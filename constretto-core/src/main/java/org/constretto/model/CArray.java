package org.constretto.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CArray extends CValue {
    private final List<CValue> data;

    public CArray(List<CValue> data) {
        this.data = data;
    }

    public List<CValue> data() {
        return data;
    }

    @Override
    public Set<String> referencedKeys() {
        Set<String> referencedKeys = new HashSet<String>();
        for (CValue value : data) {
            referencedKeys.addAll(value.referencedKeys());
        }
        return referencedKeys;
    }

    @Override
    public void replace(String key, String resolvedValue) {
        for (CValue value : data) {
            value.replace(key, resolvedValue);
        }
    }
}
