package com.happ.retrofit.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.happ.models.EventDateTimes;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDateDeserializer implements JsonDeserializer<EventDateTimes>, JsonSerializer<EventDateTimes> {

    @Override
    public EventDateTimes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        String date = jsonObject.get("date").getAsString();
        String start_time = jsonObject.get("start_time").getAsString();
        String end_time = jsonObject.get("end_time").getAsString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        EventDateTimes eventDateTimes = new EventDateTimes();
        try {
            eventDateTimes.setDate(dateFormat.parse(date));
            eventDateTimes.setStartTime(timeFormat.parse(start_time));
            eventDateTimes.setEndTime(timeFormat.parse(end_time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return eventDateTimes;
    }

    @Override
    public JsonElement serialize(EventDateTimes src, Type typeOfSrc, JsonSerializationContext context) {

        Date date = src.getDate();
        Date startTime = src.getStartTime();
        Date endTime = src.getEndTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        JsonObject eventDateTimeObj = new JsonObject();
        eventDateTimeObj.addProperty("date", dateFormat.format(date));
        eventDateTimeObj.addProperty("start_time", timeFormat.format(startTime));
        eventDateTimeObj.addProperty("end_time", timeFormat.format(endTime));
        return eventDateTimeObj;

    }
}