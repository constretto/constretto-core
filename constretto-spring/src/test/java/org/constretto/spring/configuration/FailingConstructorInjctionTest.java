package org.constretto.spring.configuration;

import org.constretto.exception.ConstrettoException;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Shows how contexts defining beans with more than one @Configure annotated constructor will fail
 *
 * @author zapodot
 */
public class FailingConstructorInjctionTest {

    @Test(expected = ConstrettoException.class)
    public void testSetup() throws Exception {
        ClassPathXmlApplicationContext classPathXmlApplicationContext =
                new ClassPathXmlApplicationContext("classpath:org/constretto/spring/configuration/FailingConstructorInjectionTest-context.xml");
        classPathXmlApplicationContext.start();

    }
}
