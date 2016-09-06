package com.happ.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dante on 7/27/16.
 */
public class EventsResponse {
    @SerializedName("results")
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
