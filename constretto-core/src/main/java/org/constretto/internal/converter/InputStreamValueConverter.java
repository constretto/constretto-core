package org.constretto.internal.converter;

import org.constretto.exception.ConstrettoConversionException;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class InputStreamValueConverter implements ValueConverter<InputStream> {

    public InputStream fromString(String resourceName) throws ConstrettoConversionException {
        try {
            return new DefaultResourceLoader(this.getClass().getClassLoader()).getResource(resourceName).getInputStream();
        } catch (IOException e) {
            throw new ConstrettoConversionException(resourceName, InputStream.class, e);
        }
    }

}