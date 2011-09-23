package org.constretto.internal.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.model.Resource;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class InputStreamValueConverter implements ValueConverter<InputStream> {

    public InputStream fromString(String resourceName) throws ConstrettoConversionException {
        return new Resource(resourceName).getInputStream();
    }
}