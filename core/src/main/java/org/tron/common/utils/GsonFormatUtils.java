package org.tron.common.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GsonFormatUtils {
    private static final Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    private static final Gson DOUBLE_FIX_GSON = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<List<Object>>() {}.getType(),
                    new DoubleTypeAdapter())
            .create();

    private GsonFormatUtils() {
    }


    /**
     * convert  Object to json string
     *
     * @param object
     * @return
     */
    public static String toGsonString(Object object) {
        return gson.toJson(object);
    }


    /**
     * convert GsonString to generic bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, cls);
    }


    /**
     * convert to List
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> gsonToList(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, new TypeToken<List<T>>() {}.getType());
    }

    public static <T> List<T> gsonToListFixDouble(String gsonString, Class<T> cls) {
        try {
            return DOUBLE_FIX_GSON.fromJson(gsonString, new TypeToken<List<Object>>() {}.getType());
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return null;
    }


    public static class DoubleTypeAdapter extends TypeAdapter<Object> {
        private static final TypeAdapter<Object> delegate = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .getAdapter(Object.class);

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out, value);
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedHashMap<>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    String n = in.nextString();
                    if (n.indexOf('.') != -1) {
                        return Double.parseDouble(n);
                    }
                    try {
                        return Long.parseLong(n);
                    } catch (NumberFormatException e) {
                        return new BigDecimal(n);
                    }

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }
    }
}
