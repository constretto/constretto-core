package org.constretto.model;

import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CPrimitive extends CValue {
    private String value;

    public CPrimitive(String value) {

        if(value == null) {
            throw new NullPointerException("The \"value\" argument can not be null");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public Iterable<String> referencedKeys() {

        final Iterator<String> iterator = new Iterator<String>() {

            Boolean hasNext;
            String s;

            @Override
            public boolean hasNext() {
                if (hasNext != null) {
                    return hasNext;
                }

                String prefix = "#{", postfix = "}";

                int startIndex = value.lastIndexOf(prefix);
                if (startIndex < 0) {
                    hasNext = false;
                    return false;
                }
                int endIndex = value.indexOf(postfix, startIndex);
                if (endIndex < 0) {
                    hasNext = false;
                    return false;
                }
                s = value.substring(startIndex + prefix.length(), endIndex);

                return true;
            }

            @Override
            public String next() {
                return s;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };

        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return iterator;
            }
        };
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
