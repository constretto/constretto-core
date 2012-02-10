package org.constretto.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public class GsonParser implements Parser {
    private final GsonBuilder builder = new GsonBuilder();

    public GsonParser() {
        builder.registerTypeAdapter(CValue.class, new JsonDeserializer<CValue>() {

            private CArray handleArray(JsonArray jsonArray) {
                List<CValue> values = new ArrayList<CValue>();
                for (JsonElement jsonElement : jsonArray) {
                    values.add(handle(jsonElement));
                }
                return new CArray(values);
            }

            private CObject handleObject(JsonObject jsonObject) {
                Map<String, CValue> values = new HashMap<String, CValue>();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    values.put(entry.getKey(), handle(entry.getValue()));
                }
                return new CObject(values);
            }

            private CValue handle(JsonElement json) {
                if (json.isJsonNull()) return null;
                else if (json.isJsonPrimitive()) return new CPrimitive(json.getAsString());
                else if (json.isJsonArray()) return handleArray(json.getAsJsonArray());
                else return handleObject(json.getAsJsonObject());
            }

            public CValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return handle(json);
            }
        });
    }

    public CValue parse(String value) {
        try {
            CValue cValue = builder.create().fromJson(value, CValue.class);
            if (cValue == null) return new CPrimitive(value);
            else return cValue;
        } catch (Exception e) {
            return new CPrimitive(value);
        }
    }

    public static void main(String[] args) {
        Pattern p = Pattern.compile("#\\{(.*?)}");
        Matcher m = p.matcher("#{base-url} dette er en #{variabel} sier nå jeg, for ikke å snakke om #{denne}");
        while (m.find()){
            String group = m.group(1);
            System.out.println(group);
        }
    }
}
