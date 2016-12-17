package com.happ.retrofit.serializers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.HappImage;
import com.happ.models.Interest;

import java.lang.reflect.Type;

/**
 * Created by iztiev on 9/6/16.
 */
public class InterestDeserializer implements JsonDeserializer<Interest>, JsonSerializer<Interest> {
    @Override
    public Interest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject interestJson = json.getAsJsonObject();
        JsonElement parent = interestJson.get("parent");
        boolean isSimple = false;
        String parentId = null;
        try {
            if (parent != null && !parent.isJsonNull()) {
                parentId = parent.getAsJsonPrimitive().getAsString();
            }
            isSimple = true;
        } catch (IllegalStateException ex) {
            Log.e("GSON_DESER", "InterestDeserializer.class > deserialize(…): " + ex.getLocalizedMessage());
        }

        Interest interest = new Interest();
        interest.setId(interestJson.get("id").getAsString());
        interest.setTitle(interestJson.get("title").getAsString());

        if (interestJson.get("image") != null && !interestJson.get("image").isJsonNull()) {
            JsonObject imageJson = interestJson.get("image").getAsJsonObject();
            HappImage image = new HappImage();
            image.setId(imageJson.get("id").getAsString());
            image.setPath(imageJson.get("path").getAsString());
            if (!imageJson.get("color").isJsonNull()) {
                image.setColor(imageJson.get("color").getAsString());
            }
            interest.setImage(image);
        }


        if (isSimple) {
            interest.setParentId(parentId);

            boolean hasChildren = false;
            try {
                if (interestJson.has("children")) {
                    JsonArray children = interestJson.getAsJsonArray("children");
                    for (int i=0; i<children.size(); i++) {
                        JsonObject child = children.get(i).getAsJsonObject();
                        Interest childInterest = new Interest();
                        childInterest.setId(child.get("id").getAsString());
                        childInterest.setTitle(child.get("title").getAsString());
                        childInterest.setParentId(interestJson.get("id").getAsString());


                        if (child.get("image") != null && !child.get("image").isJsonNull()) {
                            JsonObject imageJson = child.get("image").getAsJsonObject();
                            HappImage image = new HappImage();
                            image.setId(imageJson.get("id").getAsString());
                            image.setPath(imageJson.get("path").getAsString());
                            if (!imageJson.get("color").isJsonNull()) {
                                image.setColor(imageJson.get("color").getAsString());
                            }
                            childInterest.setImage(image);
                        }

                        interest.addChild(childInterest);
                    }
                }
            }catch (IllegalStateException ex) {
                Log.e("GSON_DESER", "InterestDeserializer.class > deserialize(…): " + ex.getLocalizedMessage());
            }

        } else {
            JsonObject parentObject = parent.getAsJsonObject();
            Interest parentInterest = new Interest();
            parentInterest.setId(parentObject.get("id").getAsString());
            parentInterest.setTitle(parentObject.get("title").getAsString());

            if (parentObject.get("image") != null && !parentObject.get("image").isJsonNull()) {
                JsonObject imageJson = parentObject.get("image").getAsJsonObject();
                HappImage image = new HappImage();
                image.setId(imageJson.get("id").getAsString());
                image.setPath(imageJson.get("path").getAsString());
                if (!imageJson.get("color").isJsonNull()) {
                    image.setColor(imageJson.get("color").getAsString());
                }
                parentInterest.setImage(image);
            }

            interest.setParent(parentInterest);
        }

        return interest;
    }

    @Override
    public JsonElement serialize(Interest src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
