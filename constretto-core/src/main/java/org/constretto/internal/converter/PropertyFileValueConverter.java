package org.constretto.internal.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.model.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class PropertyFileValueConverter implements ValueConverter<Properties> {
    private final Type listType = new TypeToken<List<String>>() {}.getType();
    private final Gson gson = new Gson();

    public Properties fromString(String resourceName) throws ConstrettoConversionException {
        try {
            Properties properties = new Properties();
            InputStream stream = new Resource(resourceName).getInputStream();
            if (resourceName.endsWith(".xml")) {
                properties.loadFromXML(stream);
            } else {
                properties.load(stream);
            }
            return properties;
        } catch (IOException e) {
            throw new ConstrettoConversionException(resourceName, Properties.class, e);
        }
    }

    public List<Properties> fromStrings(String value) throws ConstrettoConversionException {
        List<Properties> propertieses = new ArrayList<Properties>();
        List<String> names = gson.fromJson(value,listType);
        for (String name : names) {
            propertieses.add(fromString(name));
        }
        return propertieses;
    }

}