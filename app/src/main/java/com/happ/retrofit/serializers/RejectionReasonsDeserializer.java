package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.RejectionReasons;

import java.lang.reflect.Type;

/**
 * Created by dante on 12/23/16.
 */
public class RejectionReasonsDeserializer implements JsonDeserializer<RejectionReasons>, JsonSerializer<RejectionReasons> {
    @Override
    public RejectionReasons deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject rrJson = json.getAsJsonObject();
        RejectionReasons rejectionReasons = new RejectionReasons();
        rejectionReasons.setId(rrJson.get("id").getAsString());
        rejectionReasons.setText(rrJson.get("text").getAsString());
        return rejectionReasons;
    }

    @Override
    public JsonElement serialize(RejectionReasons src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
