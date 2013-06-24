package org.constretto.internal;

import org.constretto.annotation.Configure;

/**
 * Used to test @Configure annotated constructors.
 *
 * @author Sondre Eikanger Kval&oslash;
 */
public class NonLocalConfigurationClass {

    private String value;

    @Configure
    public NonLocalConfigurationClass(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}