package org.constretto.internal.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class ListValueConverter<T> implements ValueConverter<List<T>>{

    private ValueConverter<T> converter;
    private final Type listType = new TypeToken<List<String>>() {}.getType();
    private final Gson gson = new Gson();

    public ListValueConverter(ValueConverter<T> converter){
        this.converter = converter;
    }

    public List<T> fromString(String value) throws ConstrettoConversionException {
        List<String> parsed = gson.fromJson(value, listType);
        List<T> converted = new ArrayList<T>();
        for (String s : parsed) {
            converted.add(converter.fromString(s));
        }
        return converted;
    }

    public List<List<T>> fromStrings(String value) throws ConstrettoConversionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
