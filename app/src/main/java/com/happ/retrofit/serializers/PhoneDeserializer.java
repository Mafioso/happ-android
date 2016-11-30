package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.EventPhones;

import java.lang.reflect.Type;

/**
 * Created by iztiev on 9/23/16.
 */

public class PhoneDeserializer implements JsonDeserializer<EventPhones>, JsonSerializer<EventPhones> {
    @Override
    public EventPhones deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String phone = json.getAsJsonPrimitive().getAsString();
        EventPhones eventPhones = new EventPhones();
        eventPhones.setPhone(phone);
        return eventPhones;
    }

    @Override
    public JsonElement serialize(EventPhones src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getPhone());
    }
}
