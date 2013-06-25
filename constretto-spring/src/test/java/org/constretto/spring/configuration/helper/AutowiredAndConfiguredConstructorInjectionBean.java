package org.constretto.spring.configuration.helper;

import org.constretto.annotation.Configure;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author zapodot
 */
public class AutowiredAndConfiguredConstructorInjectionBean {

    private SimpleConstructorInjectableBean simpleConstructorInjectableBean;

    private String key2;

    @Autowired
    @Configure
    public AutowiredAndConfiguredConstructorInjectionBean(final SimpleConstructorInjectableBean simpleConstructorInjectableBean,
                                                          final String key2) {
        this.simpleConstructorInjectableBean = simpleConstructorInjectableBean;
        this.key2 = key2;
    }

    public SimpleConstructorInjectableBean getSimpleConstructorInjectableBean() {
        return simpleConstructorInjectableBean;
    }

    public String getKey2() {
        return key2;
    }
}
