package org.constretto.test.helper;

import org.constretto.spring.annotation.Environment;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
@Component
@Environment("springjunit")
public class Green implements Color {
    public String name() {
        return "green";
    }
}
