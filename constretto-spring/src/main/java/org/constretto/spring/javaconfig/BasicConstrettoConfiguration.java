package org.constretto.spring.javaconfig;

import org.constretto.ConstrettoConfiguration;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.ConstrettoPropertyPlaceholderConfigurer;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Useful superclass for creating Spring Java config using Constretto. Enables the
 * {@link org.constretto.spring.ConstrettoPropertyPlaceholderConfigurer} and the
 * {@link org.constretto.spring.ConfigurationAnnotationConfigurer} BeanPostProcessors which will give you direct support
 * for using @Value and @Configuration in your beans.
 *
 * @author zapodot at gmail dot com
 */
@Configuration
public abstract class BasicConstrettoConfiguration {

    /**
     * Must be overridden by subclasses to provide the actual ConstrettoConfiguration instance to use for the BeanPostProcessors
     *
     * @return
     */
    @Bean
    public abstract ConstrettoConfiguration constrettoConfiguration();

    @Bean
    public ConstrettoPropertyPlaceholderConfigurer constrettoPropertyPlaceholderConfigurer() {
        return new ConstrettoPropertyPlaceholderConfigurer(constrettoConfiguration());
    }

    @Bean
    public ConfigurationAnnotationConfigurer configurationAnnotationConfigurer() {
        return new ConfigurationAnnotationConfigurer(constrettoConfiguration(),
                                                     new DefaultAssemblyContextResolver());
    }


}
