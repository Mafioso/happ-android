package com.happ.admin.happ.models;

import java.util.List;

/**
 * Created by dante on 7/27/16.
 */
public class EventsResponse {
    private List<Events> events;

    public List<Events> getEvents() {
        return events;
    }

    public void setEvents(List<Events> events) {
        this.events = events;
    }
}
