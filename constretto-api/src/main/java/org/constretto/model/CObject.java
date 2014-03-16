package org.constretto.model;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
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
        return data == null ? Collections.<String, CValue>emptyMap() : Collections.unmodifiableMap(data);
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
        StringBuilder stringBuilder = new StringBuilder("{");
        final int length = data.entrySet().size();
        int elementsAdded = 0;
        for (Map.Entry<String, CValue> entry : data.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(':');
            stringBuilder.append(entry.getValue().toString());
            elementsAdded++;
            if(elementsAdded < length) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.append("}").toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CObject cObject = (CObject) o;

        if (data != null ? !data.equals(cObject.data) : cObject.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
