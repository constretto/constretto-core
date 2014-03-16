package org.constretto.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CPrimitive extends CValue {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\{(.*?)}");
    private String value;

    public CPrimitive(String value) {

        Objects.requireNonNull(value, "The \"value\" argument can not be null");
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public Set<String> referencedKeys() {
        Set<String> referencedKeys = new HashSet<String>();
        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        while (matcher.find()) {
            String group = matcher.group(1);
            referencedKeys.add(group);
        }
        return referencedKeys;
    }

    @Override
    public void replace(String key, String resolvedValue) {
        value = value.replaceAll("#\\{" + key + "\\}", Matcher.quoteReplacement(resolvedValue));
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CPrimitive that = (CPrimitive) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {

        return value != null ? value.hashCode() : 0;
    }
}
