package org.constretto.model;

import org.apache.commons.lang.StringUtils;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CArray cArray = (CArray) o;

        if (data != null ? !data.equals(cArray.data) : cArray.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "[" + StringUtils.join(data, ',') + "]";
    }
}
