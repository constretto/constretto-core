package org.constretto.model;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CPrimitive extends CValue {
    private String value;

    public CPrimitive(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public void replace(String key, CValue resolvedValue) {
        value = value.replace(VARIABLE_PREFIX + key + VARIABLE_SUFFIX, resolvedValue.toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
