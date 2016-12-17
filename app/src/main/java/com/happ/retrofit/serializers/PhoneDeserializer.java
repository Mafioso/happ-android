package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.EventPhone;

import java.lang.reflect.Type;

/**
 * Created by iztiev on 9/23/16.
 */

public class PhoneDeserializer implements JsonDeserializer<EventPhone>, JsonSerializer<EventPhone> {
    @Override
    public EventPhone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String phone = json.getAsJsonPrimitive().getAsString();
        EventPhone eventPhone = new EventPhone();
        eventPhone.setPhone(phone);
        return eventPhone;
    }

    @Override
    public JsonElement serialize(EventPhone src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getPhone());
    }
}
