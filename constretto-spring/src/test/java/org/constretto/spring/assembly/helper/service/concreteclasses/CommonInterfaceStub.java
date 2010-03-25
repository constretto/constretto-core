package org.constretto.spring.assembly.helper.service.concreteclasses;

import org.constretto.spring.annotation.Environment;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */

@Component
@Environment("stub")
public class CommonInterfaceStub implements CommonInterface{

    public void handle() {

    }
}
