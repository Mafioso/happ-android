package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.EventDateTimes;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<EventDateTimes>, JsonSerializer<Date> {

//    @Override
//    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
//        String date = element.getAsString();
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//
//
//        try {
//            return format.parse(date);
//
//        } catch (ParseException exp) {
//            Log.e("DATE_DESER", exp.getLocalizedMessage());
//            return null;
//        }
//    }

    @Override
    public EventDateTimes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String date = json.getAsJsonPrimitive().getAsString();
        String start_time = json.getAsString();
        String end_time = json.getAsString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        EventDateTimes eventDateTimes = new EventDateTimes();

        try {
            eventDateTimes.setDate(dateFormat.parse(date));
//            eventDateTimes.setStart_time(timeFormat.parse(start_time));
//            eventDateTimes.setEnd_time(timeFormat.parse(end_time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return eventDateTimes;
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return new JsonPrimitive(format.format(src));
    }
}