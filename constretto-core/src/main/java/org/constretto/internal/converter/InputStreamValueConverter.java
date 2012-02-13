package org.constretto.internal.converter;

import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.model.Resource;

import java.io.InputStream;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class InputStreamValueConverter implements ValueConverter<InputStream> {

    public InputStream fromString(String resourceName) throws ConstrettoConversionException {
        return Resource.create(resourceName).getInputStream();
    }
}