package com.stirling.developments.Models.Deserialize;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MyDeserializer<T> implements JsonDeserializer<T> {
    private final Class mNestedClazz;
    private final Object mNestedDeserializer;

    public MyDeserializer(Class nestedClazz, Object nestedDeserializer) {
        mNestedClazz = nestedClazz;
        mNestedDeserializer = nestedDeserializer;
    }

    @Override
    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        //Obtener el elemento "aggregations" desde el JSON parseado
        JsonElement content = je.getAsJsonObject().get("aggregations");
      //  JsonObject

        //Deserializarlo. Usar una nueva instancia de Gson para evitar recursión infinita
        GsonBuilder builder = new GsonBuilder();
        if (mNestedClazz != null && mNestedDeserializer != null) {
            builder.registerTypeAdapter(mNestedClazz, mNestedDeserializer);
        }

        return builder.create().fromJson(content, type);
    }
}
