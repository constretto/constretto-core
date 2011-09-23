package org.constretto.internal.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.constretto.ValueConverter;
import org.constretto.exception.ConstrettoConversionException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class MapValueConverter<K, V> implements ValueConverter<Map<K, V>> {

    private final ValueConverter<K> keyConverter;
    private final ValueConverter<V> valueConverter;
    private final Type mapType = new TypeToken<Map<String, String>>() {}.getType();
    private final Gson gson = new Gson();

    public MapValueConverter(ValueConverter<K> keyConverter, ValueConverter<V> valueConverter) {
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }


    public Map<K, V> fromString(String value) throws ConstrettoConversionException {
        Map<K, V> result = new HashMap<K, V>();
        Map<String, String> map = gson.fromJson(value, mapType);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(keyConverter.fromString(entry.getKey()), valueConverter.fromString(entry.getValue()));
        }
        return result;
    }
}
