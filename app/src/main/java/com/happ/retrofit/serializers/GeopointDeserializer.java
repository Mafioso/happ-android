package com.happ.retrofit.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.GeopointResponse;

import java.lang.reflect.Type;

/**
 * Created by dante on 12/5/16.
 */
public class GeopointDeserializer implements JsonDeserializer<GeopointResponse>, JsonSerializer<GeopointResponse> {
    @Override
    public GeopointResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject geoJson = json.getAsJsonObject();
        JsonArray geopointArray;
        if (json.isJsonArray()) {
            geopointArray = geoJson.getAsJsonArray();
        } else {
            geopointArray = geoJson.get("coordinates").getAsJsonArray();
        }
        GeopointResponse eventGeopoints = new GeopointResponse();
        eventGeopoints.setLat(geopointArray.get(0).getAsFloat());
        eventGeopoints.setLng(geopointArray.get(1).getAsFloat());
        return eventGeopoints;
    }

    @Override
    public JsonElement serialize(GeopointResponse src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray geoArray = new JsonArray();
        geoArray.add(new JsonPrimitive(src.getLat()));
        geoArray.add(new JsonPrimitive(src.getLng()));
        return geoArray;
    }
}

