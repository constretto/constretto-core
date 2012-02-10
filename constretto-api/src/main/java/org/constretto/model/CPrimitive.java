package org.constretto.model;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class CPrimitive extends CValue {
    private final Pattern variablePattern = Pattern.compile("#\\{(.*?)}");
    private String value;

    public CPrimitive(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public Set<String> referencedKeys() {
        Set<String> referencedKeys = new HashSet<String>();
        Matcher matcher = variablePattern.matcher(value);
        while (matcher.find()) {
            String group = matcher.group(1);
            referencedKeys.add(group);
        }
        return referencedKeys;
    }

    @Override
    public void replace(String key, String resolvedValue) {
        value = value.replaceAll("#\\{" + key + "\\}", resolvedValue);
    }

    @Override
    public String toString() {
        return value;
    }
}
