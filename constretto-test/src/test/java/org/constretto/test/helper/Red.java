package org.constretto.test.helper;

import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
@Component
public class Red implements Color {
    public String name() {
        return "red";
    }
}