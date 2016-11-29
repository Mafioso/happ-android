package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.EventImage;

import java.lang.reflect.Type;

/**
 * Created by iztiev on 9/23/16.
 */

public class ImageDeserializer implements JsonDeserializer<EventImage>, JsonSerializer<EventImage> {
    @Override
    public EventImage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String url = json.getAsJsonPrimitive().getAsString();
        EventImage eventImage = new EventImage();
        eventImage.setPath(url);
        return eventImage;
    }

    @Override
    public JsonElement serialize(EventImage src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getPath());
    }
}
