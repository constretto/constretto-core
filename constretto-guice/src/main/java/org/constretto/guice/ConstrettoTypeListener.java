package org.constretto.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.constretto.ConstrettoConfiguration;

@Singleton
public class ConstrettoTypeListener implements TypeListener {

    private final ConstrettoConfiguration constrettoConfiguration;

    @Inject
    public ConstrettoTypeListener(ConstrettoConfiguration constrettoConfiguration) {
        this.constrettoConfiguration = constrettoConfiguration;
    }

    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        typeEncounter.register(new InjectionListener<I>() {
            @Override
            public void afterInjection(I injectee) {
                constrettoConfiguration.on(injectee);
            }
        });
    }
}
