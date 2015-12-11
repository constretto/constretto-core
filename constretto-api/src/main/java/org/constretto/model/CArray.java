package org.constretto.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CArray extends CValue {
    private final List<CValue> data;

    public CArray(final List<CValue> data) {

        if(data == null) {
            throw new NullPointerException("The \"data\" argument can not be null");
        }
        this.data = Arrays.asList(data.toArray(new CValue[]{}));
    }

    public List<CValue> data() {
        return data == null ? Collections.<CValue>emptyList() : Collections.unmodifiableList(data);
    }

    @Override
    public Iterable<String> referencedKeys() {
        List<Iterable<String>> referencedKeys = new ArrayList<Iterable<String>>();
        for (CValue value : data) {
            referencedKeys.add(value.referencedKeys());
        }
        return new Iter<String>(referencedKeys.toArray(new Iterable[0]));

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
