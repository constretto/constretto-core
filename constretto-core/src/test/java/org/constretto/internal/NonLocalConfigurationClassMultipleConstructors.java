package org.constretto.internal;

import org.constretto.annotation.Configure;

/**
 *
 * @author zapodot
 */
public class NonLocalConfigurationClassMultipleConstructors {

    private String value;
    private String someOtherValue;

    @Configure
    public NonLocalConfigurationClassMultipleConstructors(final String value) {
        this.value = value;
    }

    @Configure
    public NonLocalConfigurationClassMultipleConstructors(final String value, final String someOtherValue) {
        this.value = value;
        this.someOtherValue = someOtherValue;
    }
}
