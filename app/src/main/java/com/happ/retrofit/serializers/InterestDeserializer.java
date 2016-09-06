package com.happ.retrofit.serializers;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.happ.models.Interest;

import java.lang.reflect.Type;

/**
 * Created by iztiev on 9/6/16.
 */
public class InterestDeserializer implements JsonDeserializer<Interest> {
    @Override
    public Interest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject interestJson = json.getAsJsonObject();
        JsonElement parent = interestJson.get("parent");
        boolean isSimple = false;
        String parentId = null;
        try {
            if (!parent.isJsonNull()) {
                parentId = parent.getAsJsonPrimitive().getAsString();
            }
            isSimple = true;
        } catch (IllegalStateException ex) {
            Log.e("GSON_DESER", "InterestDeserializer.class > deserialize(…): " + ex.getLocalizedMessage());
        }

        Interest interest = new Interest();
        interest.setId(interestJson.get("id").getAsString());
        interest.setTitle(interestJson.get("title").getAsString());
        if (!interestJson.get("color").isJsonNull()) {
            interest.setColor(interestJson.get("color").getAsString());
        }

        if (isSimple) {
            interest.setParentId(parentId);
        } else {
            JsonObject parentObject = parent.getAsJsonObject();
            Interest parentInterest = new Interest();
            parentInterest.setId(parentObject.get("id").getAsString());
            parentInterest.setTitle(parentObject.get("title").getAsString());
            if (!parentObject.get("color").isJsonNull()) {
                parentInterest.setColor(parentObject.get("color").getAsString());
            }
            interest.setParent(parentInterest);
        }

        return interest;
    }
}
