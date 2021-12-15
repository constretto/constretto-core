package org.constretto.model;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CArray extends CValue {
    private final List<CValue> data;

    public CArray(final List<CValue> data) {

        if (data == null) {
            throw new NullPointerException("The \"data\" argument can not be null");
        }
        this.data = Arrays.asList(data.toArray(new CValue[]{}));
    }

    public List<CValue> data() {
        return data == null ? Collections.<CValue>emptyList() : Collections.unmodifiableList(data);
    }

    @Override
    public Set<String> referencedKeys() {
        Set<String> referencedKeys = new HashSet<String>();
        for (CValue value : data) {
            if (value != null) {
                referencedKeys.addAll(value.referencedKeys());
            }
        }
        return referencedKeys;
    }

    @Override
    public void replace(String key, String resolvedValue) {
        for (CValue value : data) {
            if (value != null) {
                value.replace(key, resolvedValue);
            }
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
