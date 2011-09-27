package org.constretto.model;

import java.util.List;

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
    public void replace(String key, CValue resolvedValue) {
        for (CValue value : data) {
            value.replace(key, resolvedValue);
        }
    }
}
