package org.constretto.test.helper;

import org.springframework.stereotype.Component;
import org.constretto.spring.annotation.Environment;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
@Component
@Environment("springjunit")
public class Green implements Color{
    public String name() {
        return "green";
    }
}
