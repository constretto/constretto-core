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
    private final Type listType = new TypeToken<List<String>>() {}.getType();
    private final Gson gson = new Gson();

    public InputStream fromString(String resourceName) throws ConstrettoConversionException {
        return new Resource(resourceName).getInputStream();
    }

    public List<InputStream> fromStrings(String value) throws ConstrettoConversionException {
        List<InputStream> inputStreams = new ArrayList<InputStream>();
        List<String> inputStreamNames = gson.fromJson(value,listType);
        for (String inputStreamName : inputStreamNames) {
            inputStreams.add(fromString(inputStreamName));
        }
        return inputStreams;
    }

}