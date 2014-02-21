package org.constretto.spring.annotation;

import org.constretto.spring.internal.ConstrettoImportRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Used to setup Constretto Spring BeanPostProcessors in Java-based Spring context.
 * <p/>
 * If you provide this annotation without any arguments it will search for a public static non-arg method in your
 * configuration class that returns a {@link org.constretto.ConstrettoConfiguration} instance
 * (hint: use {@link org.constretto.ConstrettoBuilder} to create one).
 *
 * @author zapodot at gmail dot com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ConstrettoImportRegistrar.class)
@Documented
public @interface Constretto {

    /**
     * Should the Constretto based property placeholder BeanPostProcessor be enabled? Default is true
     */
    boolean enablePropertyPlaceholder() default true;

    /**
     * Should the Constretto annotation (for injecting configuration for {@link org.constretto.annotation.Configuration}
     * and {@link org.constretto.annotation.Configure} annotated fields and methods.
     */
    boolean enableAnnotationSupport() default true;


}
