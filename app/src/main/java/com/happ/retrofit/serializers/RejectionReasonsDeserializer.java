package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.RejectionReason;

import java.lang.reflect.Type;

/**
 * Created by dante on 12/23/16.
 */
public class RejectionReasonsDeserializer implements JsonDeserializer<RejectionReason>, JsonSerializer<RejectionReason> {
    @Override
    public RejectionReason deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject rrJson = json.getAsJsonObject();
        RejectionReason rejectionReason = new RejectionReason();
        rejectionReason.setId(rrJson.get("id").getAsString());
        rejectionReason.setText(rrJson.get("text").getAsString());
        return rejectionReason;
    }

    @Override
    public JsonElement serialize(RejectionReason src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
