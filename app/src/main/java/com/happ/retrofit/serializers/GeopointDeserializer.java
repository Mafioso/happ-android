package com.happ.retrofit.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.GeopointArrayResponce;

import java.lang.reflect.Type;

/**
 * Created by dante on 12/5/16.
 */
public class GeopointDeserializer implements JsonSerializer<GeopointArrayResponce> {
//    @Override
//    public GeopointResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject geoJson = json.getAsJsonObject();
//        JsonArray geopointArray;
////        if (json.isJsonArray()) {
//            geopointArray = geoJson.getAsJsonArray();
////        } else {
////            if (geoJson.get("coordinates").isJsonArray()) {
////                geopointArray = geoJson.get("coordinates").getAsJsonArray();
////            } else {
////                geoJson = geoJson.get("coordinates").getAsJsonObject();
////                geopointArray = geoJson.get("coordinates").getAsJsonArray();
////            }
////        }
//        GeopointResponse eventGeopoints = new GeopointResponse();
//        eventGeopoints.setLat(geopointArray.get(0).getAsFloat());
//        eventGeopoints.setLng(geopointArray.get(1).getAsFloat());
//        return null;
//    }

    @Override
    public JsonElement serialize(GeopointArrayResponce src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray geoArray = new JsonArray();

        geoArray.add(new JsonPrimitive(src.getLng()));
        geoArray.add(new JsonPrimitive(src.getLat()));

        return geoArray;
    }
}