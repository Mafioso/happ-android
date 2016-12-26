package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UserDateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String dateJson = element.getAsString();

        SimpleDateFormat longDateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        longDateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (dateJson.equals(onlyDate.format(onlyDate.parse(dateJson)))) {
                return onlyDate.parse(dateJson);
            }
            return longDateFormater.parse(dateJson);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return new JsonPrimitive(format.format(src));
    }
}