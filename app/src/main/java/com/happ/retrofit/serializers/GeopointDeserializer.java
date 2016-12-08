package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.Geopoints;

import java.lang.reflect.Type;

/**
 * Created by dante on 12/5/16.
 */
    public class GeopointDeserializer implements JsonDeserializer<Geopoints>, JsonSerializer<Geopoints> {
        @Override
        public Geopoints deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Float lat = json.getAsJsonPrimitive().getAsFloat();
            Float lng = json.getAsJsonPrimitive().getAsFloat();
            Geopoints eventGeopoints = new Geopoints();
            eventGeopoints.setLat(lat);
            eventGeopoints.setLng(lng);
            return eventGeopoints;
        }

        @Override
        public JsonElement serialize(Geopoints src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }
    }

